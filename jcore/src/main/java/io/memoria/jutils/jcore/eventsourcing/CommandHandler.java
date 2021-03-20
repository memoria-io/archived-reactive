package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import io.memoria.jutils.jcore.msgbus.MsgBusPublisher;
import io.memoria.jutils.jcore.text.TextTransformer;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ClassCanBeRecord")
public class CommandHandler<S, C extends Command> implements Function1<C, Mono<Void>> {
  public static <S> Mono<ConcurrentHashMap<Id, S>> buildState(Flux<Event> events, Evolver<S> evolver) {
    ConcurrentHashMap<Id, S> db = new ConcurrentHashMap<>();
    return events.map(event -> db.compute(event.aggId(), (k, oldValue) -> evolver.apply(oldValue, event)))
                 .then(Mono.just(db));
  }

  private final ConcurrentHashMap<Id, S> db;
  private final Decider<S, C> decider;
  private final MsgBusPublisher publisher;
  private final Evolver<S> evolver;
  private final S initState;
  private final TextTransformer transformer;

  public CommandHandler(ConcurrentHashMap<Id, S> db,
                        Decider<S, C> decider,
                        MsgBusPublisher publisher,
                        Evolver<S> evolver,
                        S initState,
                        TextTransformer transformer) {
    this.db = db;
    this.decider = decider;
    this.publisher = publisher;
    this.evolver = evolver;
    this.initState = initState;
    this.transformer = transformer;
  }

  /**
   * @return mono of events batch that were successfully published after applying the command or empty mono if no
   * aggregate was found
   */
  @Override
  public Mono<Void> apply(C cmd) {
    return Mono.fromCallable(() -> {
      var s = db.getOrDefault(cmd.aggId(), initState);
      var events = decider.apply(s, cmd).get();
      var msgs = events.map(transformer::serialize).map(Try::get);
      return publish(msgs).then(Mono.<Void>fromRunnable(() -> persist(s, cmd, events)));
    }).flatMap(mono -> mono);
  }

  private Mono<Void> publish(List<String> msgs) {
    return publisher.beginTransaction()
                    .thenMany(Flux.fromIterable(msgs))
                    .concatMap(publisher::publish)
                    .then(publisher.commitTransaction());
  }

  private void persist(S s, C command, List<Event> events) {
    var newState = events.foldLeft(s, evolver);
    db.put(command.aggId(), newState);
  }
}
