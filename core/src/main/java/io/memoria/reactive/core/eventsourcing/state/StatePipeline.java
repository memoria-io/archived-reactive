package io.memoria.reactive.core.eventsourcing.state;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventStream;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import io.vavr.Function1;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public class StatePipeline implements Function1<Command, Mono<State>> {
  private final State initState;
  private final Map<Id, State> stateMap;
  private final EventStream eventStream;
  private final StateDecider stateDecider;
  private final StateEvolver evolver;

  public StatePipeline(State initState,
                       Map<Id, State> stateMap,
                       EventStream eventStream,
                       StateDecider stateDecider,
                       StateEvolver stateEvolver) {
    this.initState = initState;
    this.stateMap = stateMap;
    this.eventStream = eventStream;
    this.stateDecider = stateDecider;
    this.evolver = stateEvolver;
  }

  public Mono<State> apply(Command cmd) {
    return Mono.fromCallable(() -> stateOrInit(cmd.stateId()))
               .map(state -> stateDecider.apply(state, cmd))
               .flatMap(ReactorVavrUtils::toMono)
               .flatMap(eventStream::publish)
               .map(this::evolve);
  }

  /**
   * Side effect function that reads eventstream repo to the current size and uses stateRepo to persist all the evolved
   * states
   *
   * @return a flux of all states evolutions
   */
  public Flux<State> rebuildStates(int skip) {
    return eventStream.size().flatMapMany(idxSize -> eventStream.subscribe(skip).take(idxSize)).map(this::evolve);
  }

  private State evolve(Event event) {
    Id stateId = event.stateId();
    var currentState = stateOrInit(stateId);
    var newState = evolver.apply(currentState, event);
    stateMap.put(stateId, newState);
    return newState;
  }

  private State stateOrInit(Id stateId) {
    return Option.of(stateMap.get(stateId)).getOrElse(initState);
  }
}
