package io.memoria.reactive.core.eventsourcing.state;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandStream;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventStream;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.id.Id;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.Map;
import java.util.logging.Level;

import static io.memoria.reactive.core.vavr.ReactorVavrUtils.toMono;
import static reactor.core.publisher.SignalType.ON_COMPLETE;
import static reactor.core.publisher.SignalType.ON_ERROR;
import static reactor.core.publisher.SignalType.ON_NEXT;

public class StatePipeline {
  private final State initState;
  private final Map<Id, State> stateMap;
  private final CommandStream commandStream;
  private final EventStream eventStream;
  private final StateDecider stateDecider;
  private final StateEvolver evolver;
  private final Level logLevel;
  private final SignalType[] signalType;

  public StatePipeline(State initState,
                       Map<Id, State> stateMap,
                       CommandStream commandStream,
                       EventStream eventStream,
                       StateDecider stateDecider,
                       StateEvolver stateEvolver) {
    this(initState,
         stateMap,
         commandStream,
         eventStream,
         stateDecider,
         stateEvolver,
         Level.INFO,
         ON_NEXT,
         ON_ERROR,
         ON_COMPLETE);
  }

  public StatePipeline(State initState,
                       Map<Id, State> stateMap,
                       CommandStream commandStream,
                       EventStream eventStream,
                       StateDecider stateDecider,
                       StateEvolver stateEvolver,
                       Level logLevel,
                       SignalType... signalType) {
    this.initState = initState;
    this.stateMap = stateMap;
    this.commandStream = commandStream;
    this.eventStream = eventStream;
    this.stateDecider = stateDecider;
    this.evolver = stateEvolver;
    this.logLevel = logLevel;
    this.signalType = signalType;
  }

  /**
   * @param commandsOffset the offset when reading commands
   * @return Events Ids Flux
   */
  public Flux<Id> run(long commandsOffset) {
    var events = commandStream.subscribe(commandsOffset)
                              .log("Inbound Command", logLevel, signalType)
                              .concatMap(this::decide)
                              .log("New Event", logLevel, signalType)
                              .doOnNext(this::evolveState);
    var pubEvents = eventStream.publish(events);
    return buildStates().map(Event::id).concatWith(pubEvents);
  }

  private Flux<Event> buildStates() {
    return eventStream.size()
                      .flatMapMany(this::readEvents)
                      .log("Inbound Event", logLevel, signalType)
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
