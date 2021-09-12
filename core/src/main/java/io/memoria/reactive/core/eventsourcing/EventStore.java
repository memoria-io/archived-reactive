package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.db.Write;
import io.memoria.reactive.core.id.Id;
import io.vavr.Function1;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@SuppressWarnings("ClassCanBeRecord")
public class EventStore implements Function1<Command, Mono<State>> {

  private final transient ConcurrentMap<Id, State> state;
  private final transient State defaultState;
  private final transient Write<Event> eventRepo;
  private final Decider decider;
  private final Evolver evolver;

  public EventStore(State defaultState,
                    ConcurrentMap<Id, State> state,
                    Write<Event> eventRepo,
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
    return Mono.fromCallable(() -> pipeline(cmd)).flatMap(Function.identity());
  }

  private Mono<State> pipeline(Command cmd) {
    var aggId = cmd.aggId();
    var currentState = state.getOrDefault(aggId, defaultState);
    return decider.apply(currentState, cmd).flatMap(eventRepo::write).map(events -> save(aggId, currentState, events));
  }

  private State save(Id aggId, State currentState, List<Event> events) {
    var newState = events.foldLeft(currentState, evolver);
    state.put(aggId, newState);
    return newState;
  }
}
