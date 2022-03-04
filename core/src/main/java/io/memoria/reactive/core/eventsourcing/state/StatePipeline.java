package io.memoria.reactive.core.eventsourcing.state;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandStream;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventStream;
import io.memoria.reactive.core.eventsourcing.PipelineConfig;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.id.Id;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.HashMap;
import java.util.Map;

import static io.memoria.reactive.core.vavr.ReactorVavrUtils.toMono;

public class StatePipeline {
  private static final Logger LOGGER = Loggers.getLogger(StatePipeline.class.getName());
  private final State initState;
  private final Map<Id, State> stateMap;
  private final PipelineConfig config;
  private final CommandStream commandStream;
  private final EventStream eventStream;
  private final StateDecider stateDecider;
  private final StateEvolver evolver;

  public StatePipeline(State initState,
                       CommandStream commandStream,
                       EventStream eventStream,
                       StateDecider stateDecider,
                       StateEvolver stateEvolver) {
    this(PipelineConfig.DEFAULT, initState, commandStream, eventStream, stateDecider, stateEvolver);
  }

  public StatePipeline(PipelineConfig config,
                       State initState,
                       CommandStream commandStream,
                       EventStream eventStream,
                       StateDecider stateDecider,
                       StateEvolver stateEvolver) {
    this.config = config;
    this.initState = initState;
    this.stateMap = new HashMap<>();
    this.commandStream = commandStream;
    this.eventStream = eventStream;
    this.stateDecider = stateDecider;
    this.evolver = stateEvolver;
  }

  /**
   * @param commandsOffset the offset when reading commands
   * @return Events Ids Flux
   */
  public Flux<Id> run(long commandsOffset) {
    var events = commandStream.subscribe(commandsOffset)
                              .log(LOGGER, config.logLevel(), config.showLine(), config.signalTypeArray())
                              .concatMap(this::decide)
                              .log(LOGGER, config.logLevel(), config.showLine(), config.signalTypeArray())
                              .doOnNext(this::evolveState);
    var pubEvents = eventStream.publish(events);
    return buildStates().map(Event::id).concatWith(pubEvents);
  }

  private Flux<Event> buildStates() {
    return eventStream.size()
                      .flatMapMany(this::readEvents)
                      .log(LOGGER, config.logLevel(), config.showLine(), config.signalTypeArray())
                      .doOnNext(this::evolveState);
  }

  private Mono<Event> decide(Command cmd) {
    return toMono(stateDecider.apply(stateOrInit(cmd.stateId()), cmd));
  }

  private void evolveState(Event event) {
    Id stateId = event.stateId();
    var currentState = stateOrInit(stateId);
    var newState = evolver.apply(currentState, event);
    stateMap.put(stateId, newState);
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
