package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.rsdb.Pub;
import io.memoria.reactive.core.id.Id;
import io.vavr.Function1;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static io.memoria.reactive.core.vavr.ReactorVavrUtils.toMono;

@SuppressWarnings("ClassCanBeRecord")
public class EventStore implements Function1<Command, Mono<State>> {

  private final transient ConcurrentMap<Id, State> state;
  private final transient State defaultState;
  private final transient Pub<Event> pub;
  private final Decider decider;
  private final Evolver evolver;

  public EventStore(State defaultState,
                    ConcurrentMap<Id, State> state,
                    Pub<Event> pub,
                    Decider decider,
                    Evolver evolver) {
    this.state = state;
    this.defaultState = defaultState;
    this.pub = pub;
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
    var eventMono = toMono(decider.apply(currentState, cmd));
    return eventMono.flatMap(pub::publish).map(e -> save(aggId, currentState, e));
  }

  private State save(Id aggId, State currentState, Event event) {
    var newState = evolver.apply(currentState, event);
    state.put(aggId, newState);
    return newState;
  }
}
