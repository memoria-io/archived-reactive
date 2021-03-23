package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import io.vavr.Function1;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class CommandHandler<S, C extends Command> implements Function1<C, Mono<S>> {
  public static <S> Mono<ConcurrentHashMap<Id, S>> buildState(EventStore eventStore, Evolver<S> evolver) {
    ConcurrentHashMap<Id, S> db = new ConcurrentHashMap<>();
    return eventStore.subscribeToLast()
                     .map(event -> db.compute(event.aggId(), (k, oldValue) -> evolver.apply(oldValue, event)))
                     .then(Mono.just(db));
  }

  private final ConcurrentHashMap<Id, S> stateStore;
  private final S initState;
  private final EventStore eventStore;
  private final Decider<S, C> decider;
  private final Evolver<S> evolver;

  public CommandHandler(S initState, EventStore eventStore, Decider<S, C> decider, Evolver<S> evolver) {
    this.stateStore = new ConcurrentHashMap<>();
    this.initState = initState;
    this.eventStore = eventStore;
    this.decider = decider;
    this.evolver = evolver;
  }

  /**
   * @return mono of the new State after applying such command on it.
   */
  @Override
  public Mono<S> apply(C cmd) {
    return Mono.fromCallable(() -> {
      var currentState = stateStore.getOrDefault(cmd.aggId(), initState);
      var events = decider.apply(currentState, cmd).get();
      return publish(events).then(Mono.fromCallable(() -> persist(currentState, cmd, events)));
    }).flatMap(Function.identity());
  }

  private S persist(S currentState, C cmd, List<Event> events) {
    var newState = events.foldLeft(currentState, evolver);
    stateStore.put(cmd.aggId(), newState);
    return newState;
  }

  private Mono<Void> publish(List<Event> msgs) {
    return eventStore.publish(msgs).then();
  }
}
