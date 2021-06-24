package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.repo.EventRepo;
import io.memoria.jutils.jcore.id.Id;
import io.vavr.Function1;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@SuppressWarnings("ClassCanBeRecord")
public class EventStore<S, C extends Command> implements Function1<C, Mono<S>> {

  private final transient ConcurrentMap<Id, S> stateStore;
  private final transient S defaultState;
  private final transient EventRepo eventRepo;
  private final Decider<S, C> decider;
  private final Evolver<S> evolver;

  public EventStore(S defaultState,
                    ConcurrentMap<Id, S> stateStore,
                    EventRepo eventRepo,
                    Decider<S, C> decider,
                    Evolver<S> evolver) {
    this.stateStore = stateStore;
    this.defaultState = defaultState;
    this.eventRepo = eventRepo;
    this.decider = decider;
    this.evolver = evolver;
  }

  /**
   * @return mono of the new State after applying such command on it.
   */
  public Mono<S> apply(C cmd) {
    return Mono.fromCallable(() -> {
      var currentState = stateStore.getOrDefault(cmd.aggId(), defaultState);
      var events = decider.apply(currentState, cmd).get();
      var add = eventRepo.add(cmd.aggId().value(), events);
      var persist = Mono.fromCallable(() -> persist(currentState, cmd, events));
      return add.then(persist);
    }).flatMap(Function.identity());
  }

  private S persist(S currentState, C cmd, List<Event> events) {
    var newState = events.foldLeft(currentState, evolver);
    stateStore.put(cmd.aggId(), newState);
    return newState;
  }
}
