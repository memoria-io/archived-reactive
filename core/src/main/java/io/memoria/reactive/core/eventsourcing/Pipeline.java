package io.memoria.reactive.core.eventsourcing;

import io.vavr.Function1;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Pipeline implements Function1<Command, Mono<State>> {

  private final transient State initState;
  private final transient StateStore stateStore;
  private final transient EventStore eventStore;

  private final Decider decider;
  private final Evolver evolver;

  public Pipeline(State initState, StateStore stateStore, EventStore eventStore, Decider decider, Evolver evolver) {
    this.initState = initState;
    this.stateStore = stateStore;
    this.eventStore = eventStore;
    this.decider = decider;
    this.evolver = evolver;
  }

  /**
   * @return mono of the new State after applying such command on it.
   */
  public Mono<State> apply(Command cmd) {
    return stateStore.get(cmd.aggId(), initState).flatMap(state -> applyCommand(state, cmd));
  }
  
  public Flux<State> rebuildStateStore(long until) {
    return eventStore.subscribe(0).take(until).flatMap(this::evolveState);
  }

  private Mono<State> applyCommand(State state, Command cmd) {
    var decision = decider.apply(state, cmd);
    if (decision.isFailure())
      return Mono.error(decision.getCause());
    else
      return eventStore.publish(decision.get()).flatMap(event -> evolveState(state, event));
  }

  private Mono<State> evolveState(Event event) {
    return stateStore.get(event.aggId(), initState).flatMap(st -> evolveState(st, event));
  }

  private Mono<State> evolveState(State state, Event event) {
    var newState = evolver.apply(state, event);
    return stateStore.put(event.aggId(), newState);
  }
}
