package io.memoria.reactive.core.eventsourcing.state;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandStream;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventStream;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public class StatePipeline {
  private final State initState;
  private final Map<Id, State> stateMap;
  private final CommandStream commandStream;
  private final EventStream eventStream;
  private final StateDecider stateDecider;
  private final StateEvolver evolver;

  public StatePipeline(State initState,
                       Map<Id, State> stateMap,
                       CommandStream commandStream,
                       EventStream eventStream,
                       StateDecider stateDecider,
                       StateEvolver stateEvolver) {
    this.initState = initState;
    this.stateMap = stateMap;
    this.commandStream = commandStream;
    this.eventStream = eventStream;
    this.stateDecider = stateDecider;
    this.evolver = stateEvolver;
  }

  /**
   * @param commandsOffset the offset when reading commands
   * @return all states evolutions concatenated with any new commands
   */
  public Flux<State> run(long commandsOffset) {
    var cmds = commandStream.subscribe(commandsOffset).concatMap(this::apply);
    return buildStates().concatWith(cmds);
  }

  private Mono<State> apply(Command cmd) {
    return Mono.fromCallable(() -> stateOrInit(cmd.stateId()))
               .map(state -> stateDecider.apply(state, cmd))
               .flatMap(ReactorVavrUtils::toMono)
               .flatMap(eventStream::publish)
               .map(this::evolve);
  }

  private Flux<State> buildStates() {
    return eventStream.size().flatMapMany(this::readEvents).map(this::evolve);
  }

  private State evolve(Event event) {
    Id stateId = event.stateId();
    var currentState = stateOrInit(stateId);
    var newState = evolver.apply(currentState, event);
    stateMap.put(stateId, newState);
    return newState;
  }

  private Flux<Event> readEvents(long idxSize) {
    if (idxSize > 0)
      return eventStream.subscribe(0).take(idxSize);
    else
      return Flux.empty();
  }

  private State stateOrInit(Id stateId) {
    return Option.of(stateMap.get(stateId)).getOrElse(initState);
  }
}
