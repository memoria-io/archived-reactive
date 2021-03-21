package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.msgbus.MsgBus;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

public record CommandHandler<S, C extends Command>(S initState,
                                                   MsgBus msgBus,
                                                   ConcurrentHashMap<Id, S> stateStore,
                                                   TextTransformer transformer,
                                                   Decider<S, C> decider,
                                                   Evolver<S> evolver) implements Function1<C, Mono<Void>> {

  public static <S> Mono<ConcurrentHashMap<Id, S>> evolve(String topic,
                                                          int partition,
                                                          MsgBus msgBus,
                                                          TextTransformer transformer,
                                                          Evolver<S> evolver) {
    ConcurrentHashMap<Id, S> db = new ConcurrentHashMap<>();
    msgBus.currentOffset(topic, partition)
          .map(offset -> offset - 2)
          .map(msgBus::subscribe)
          .map(msg -> transformer.deserialize(msg, Event.class));
    return msgBus.subscribe()
                 .map(msg -> transformer.deserialize(msg, Event.class))
                 .map(Try::get)
                 .map(event -> db.compute(event.aggId(), (k, oldValue) -> evolver.apply(oldValue, event)))
                 .then(Mono.just(db));
  }

  /**
   * @return mono of events batch that were successfully published after applying the command or empty mono if no
   * aggregate was found
   */
  @Override
  public Mono<Void> apply(C cmd) {
    return Mono.fromCallable(() -> {
      var s = stateStore.getOrDefault(cmd.aggId(), initState);
      var events = decider.apply(s, cmd).get();
      var msgs = events.map(transformer::serialize).map(Try::get);
      return publish(msgs).then(Mono.<Void>fromRunnable(() -> persist(s, cmd, events)));
    }).flatMap(mono -> mono);
  }

  private void persist(S s, C command, List<Event> events) {
    var newState = events.foldLeft(s, evolver);
    stateStore.put(command.aggId(), newState);
  }

  private Mono<Void> publish(List<String> msgs) {
    return msgBus.beginTransaction()
                 .thenMany(Flux.fromIterable(msgs))
                 .concatMap(msgBus::publish)
                 .then(msgBus.commitTransaction());
  }
}
