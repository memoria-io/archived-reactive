package io.memoria.reactive.core.eventsourcing.state;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.LogConfig;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StreamConfig;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.text.TextTransformer;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class StatePipeline {
  private static final Logger LOGGER = Loggers.getLogger(StatePipeline.class.getName());
  // Infra
  private final Stream stream;
  private final TextTransformer transformer;
  private final Set<Id> processedCmds;
  private final Map<Id, State> stateRepo;
  // Business logic
  private final State initState;
  private final StateDecider stateDecider;
  private final StateEvolver evolver;
  // Configs
  private final StreamConfig commandConfig;
  private final StreamConfig eventConfig;
  private final LogConfig logConfig;

  public StatePipeline(Stream stream,
                       TextTransformer transformer,
                       State initState,
                       StateDecider stateDecider,
                       StateEvolver evolver,
                       StreamConfig commandConfig,
                       StreamConfig eventConfig,
                       LogConfig logConfig) {
    // Infra
    this.stream = stream;
    this.transformer = transformer;
    this.processedCmds = new HashSet<>();
    this.stateRepo = new ConcurrentHashMap<>();
    // Business logic
    this.initState = initState;
    this.stateDecider = stateDecider;
    this.evolver = evolver;
    // Configs
    this.commandConfig = commandConfig;
    this.eventConfig = eventConfig;
    this.logConfig = logConfig;
  }

  public Flux<Event> run() {
    var events = streamCommands().map(this::decide)
                                 .concatMap(ReactorVavrUtils::toMono)
                                 .doOnNext(this::evolveState)
                                 .doOnNext(event -> processedCmds.add(event.commandId()));
    return buildStates().concatWith(publishEvents(events));
  }

  private Flux<Event> buildStates() {
    return stream.size(eventConfig.topic(), eventConfig.partition())
                 .flatMapMany(this::readEvents)
                 .doOnNext(this::evolveState);
  }

  private Try<Event> decide(Command cmd) {
    var state = stateOrInit(cmd.stateId());
    return stateDecider.apply(state, cmd);
  }

  private void evolveState(Event event) {
    var currentState = stateOrInit(event.stateId());
    var newState = evolver.apply(currentState, event);
    stateRepo.put(event.stateId(), newState);
  }

  private Flux<Event> publishEvents(Flux<Event> events) {
    return stream.publish(events.concatMap(this::toMsg))
                 .concatMap(this::toEvent)
                 .log(LOGGER, Level.INFO, logConfig.showLine(), logConfig.signalTypeArray());
  }

  private Flux<Event> readEvents(long until) {
    if (until > 0)
      return stream.subscribe(eventConfig.topic(), eventConfig.partition(), eventConfig.offset())
                   .take(until)
                   .concatMap(this::toEvent)
                   .log(LOGGER, Level.INFO, logConfig.showLine(), logConfig.signalTypeArray());
    else
      return Flux.empty();
  }

  private State stateOrInit(Id stateId) {
    return Option.of(stateRepo.get(stateId)).getOrElse(initState);
  }

  private Flux<Command> streamCommands() {
    return stream.subscribe(commandConfig.topic(), commandConfig.partition(), commandConfig.offset())
                 .concatMap(this::toCommand)
                 .filter(cmd -> !processedCmds.contains(cmd.id()))
                 .log(LOGGER, Level.INFO, logConfig.showLine(), logConfig.signalTypeArray());
  }

  private Mono<Command> toCommand(Msg msg) {
    return transformer.deserialize(msg.value(), Command.class);
  }

  private Mono<Event> toEvent(Msg msg) {
    return transformer.deserialize(msg.value(), Event.class);
  }

  private Mono<Msg> toMsg(Event event) {
    return transformer.serialize(event)
                      .map(body -> new Msg(eventConfig.topic(), eventConfig.partition(), event.id(), body));
  }
}
