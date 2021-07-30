package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.repo.EventRepo;
import io.memoria.jutils.jcore.id.Id;
import io.vavr.Function1;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@SuppressWarnings("ClassCanBeRecord")
public class EventStore implements Function1<Command, Mono<State>> {
  public static Mono<ConcurrentHashMap<Id, State>> buildState(EventRepo eventRepo, Evolver evolver) {
    var state = new ConcurrentHashMap<Id, State>();
    return eventRepo.find().doOnNext(events -> buildState(state, evolver, events)).then(Mono.just(state));
  }

  private final transient ConcurrentMap<Id, State> state;
  private final transient State defaultState;
  private final transient EventRepo eventRepo;
  private final Decider decider;
  private final Evolver evolver;

  public EventStore(State defaultState,
                    ConcurrentMap<Id, State> state,
                    EventRepo eventRepo,
                    Decider decider,
                    Evolver evolver) {
    this.state = state;
    this.defaultState = defaultState;
    this.eventRepo = eventRepo;
    this.decider = decider;
    this.evolver = evolver;
  }

  /**
   * @return mono of the new State after applying such command on it.
   */
  public Mono<State> apply(Command cmd) {
    return Mono.fromCallable(() -> {
      var currentState = state.getOrDefault(cmd.aggId(), defaultState);
      var events = decider.apply(currentState, cmd).get();
      var add = eventRepo.add(events);
      var persist = Mono.fromCallable(() -> persist(currentState, cmd, events));
      return add.then(persist);
    }).flatMap(Function.identity());
  }

  private State persist(State currentState, Command cmd, List<Event> events) {
    var newState = events.foldLeft(currentState, evolver);
    state.put(cmd.aggId(), newState);
    return newState;
  }

  private static void buildState(ConcurrentHashMap<Id, State> stateStore, Evolver evolver, List<Event> events) {
    events.forEach(event -> stateStore.compute(event.aggId(), (k, oldV) -> evolver.apply(oldV, event)));
  }
}
