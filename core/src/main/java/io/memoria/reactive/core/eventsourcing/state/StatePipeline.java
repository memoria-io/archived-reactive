package io.memoria.reactive.core.eventsourcing.state;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.PipelineConfig;
import io.memoria.reactive.core.eventsourcing.PipelineConfig.LogConfig;
import io.memoria.reactive.core.eventsourcing.PipelineConfig.StreamConfig;
import io.memoria.reactive.core.eventsourcing.State;
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

public class StatePipeline {
  private static final Logger LOGGER = Loggers.getLogger(StatePipeline.class.getName());
  // Infra
  private final Stream stream;
  private final Set<Id> processedCmds;
  private final Map<Id, State> stateRepo;
  // Business logic
  private final State initState;
  private final StateDecider stateDecider;
  private final StateEvolver evolver;
  private final TextTransformer transformer;
  // Configs
  private final StreamConfig commandConfig;
  private final StreamConfig streamConfig;
  private final LogConfig logConfig;

  public StatePipeline(Stream stream,
                       State initState,
                       StateDecider stateDecider,
                       StateEvolver evolver,
                       PipelineConfig config,
                       TextTransformer transformer) {
    // Infra
    this.stream = stream;
    this.processedCmds = new HashSet<>();
    this.stateRepo = new ConcurrentHashMap<>();
    // Business logic
    this.initState = initState;
    this.stateDecider = stateDecider;
    this.evolver = evolver;
    this.transformer = transformer;
    // Configs
    this.commandConfig = config.commandConfig();
    this.streamConfig = config.eventConfig();
    this.logConfig = config.logConfig();
  }

  public Flux<Event> run() {
    var msgs = streamCommands().map(this::decide)
                               .concatMap(ReactorVavrUtils::toMono)
                               .doOnNext(this::evolveState)
                               .doOnNext(event -> processedCmds.add(event.commandId()))
                               .concatMap(this::toMsg);
    var msgsPublished = stream.publish(msgs).concatMap(this::toEvent);
    return buildStates().concatWith(msgsPublished);
  }

  private Flux<Event> buildStates() {
    return stream.size(streamConfig.topic(), streamConfig.partition())
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

  private Flux<Event> readEvents(long idxSize) {
    if (idxSize > 0)
      return stream.subscribe(streamConfig.topic(), streamConfig.partition(), streamConfig.offset())
                   .log(LOGGER, logConfig.logLevel(), logConfig.showLine(), logConfig.signalTypeArray())
                   .take(idxSize)
                   .concatMap(this::toEvent);
    else
      return Flux.empty();
  }

  private State stateOrInit(Id stateId) {
    return Option.of(stateRepo.get(stateId)).getOrElse(initState);
  }

  private Flux<Command> streamCommands() {
    return stream.subscribe(commandConfig.topic(), commandConfig.partition(), commandConfig.offset())
                 .log(LOGGER, logConfig.logLevel(), logConfig.showLine(), logConfig.signalTypeArray())
                 .concatMap(this::toCommand)
                 .filter(cmd -> processedCmds.contains(cmd.id()));
  }

  private Mono<Command> toCommand(Msg msg) {
    return transformer.deserialize(msg.value(), Command.class);
  }

  private Mono<Event> toEvent(Msg msg) {
    return transformer.deserialize(msg.value(), Event.class);
  }

  private Mono<Msg> toMsg(Event event) {
    return transformer.serialize(event)
                      .map(body -> new Msg(streamConfig.topic(), streamConfig.partition(), event.id(), body));
  }
}
