package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import io.vavr.Function1;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

public record CommandHandler<S, C extends Command>(S initState,
                                                   EventStore eventStore,
                                                   ConcurrentHashMap<Id, S> stateStore,
                                                   Decider<S, C> decider,
                                                   Evolver<S> evolver) implements Function1<C, Mono<Void>> {

  public static <S> Mono<ConcurrentHashMap<Id, S>> evolve(EventStore eventStore, Evolver<S> evolver) {
    ConcurrentHashMap<Id, S> db = new ConcurrentHashMap<>();
    return eventStore.subscribeToLast()
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
      return publish(events).then(Mono.<Void>fromRunnable(() -> persist(s, cmd, events)));
    }).flatMap(mono -> mono);
  }

  private void persist(S s, C command, List<Event> events) {
    var newState = events.foldLeft(s, evolver);
    stateStore.put(command.aggId(), newState);
  }

  private Mono<Void> publish(List<Event> msgs) {
    return eventStore.publish(msgs).then();
  }
}
