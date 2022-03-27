package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
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
  private final PipelineRoute pipelineRoute;
  private final PipelineLogConfig pipelineLogConfig;

  public StatePipeline(Stream stream,
                       TextTransformer transformer,
                       State initState,
                       StateDecider stateDecider,
                       StateEvolver evolver,
                       PipelineRoute pipelineRoute,
                       PipelineLogConfig pipelineLogConfig) {
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
    this.pipelineRoute = pipelineRoute;
    this.pipelineLogConfig = pipelineLogConfig;
  }

  public Flux<Event> run() {
    var events = streamCommands().map(this::decide)
                                 .concatMap(ReactorVavrUtils::toMono)
                                 .doOnNext(this::evolveState)
                                 .doOnNext(event -> processedCmds.add(event.commandId()));
    return buildStates().concatWith(publishEvents(events));
  }

  public State stateOrInit(Id stateId) {
    return Option.of(stateRepo.get(stateId)).getOrElse(initState);
  }

  public Mono<Command> toCommand(Msg msg) {
    return transformer.deserialize(msg.value(), Command.class);
  }

  public Mono<Event> toEvent(Msg msg) {
    return transformer.deserialize(msg.value(), Event.class);
  }

  public Mono<Msg> toMsg(Event event) {
    return transformer.serialize(event)
                      .map(body -> new Msg(pipelineRoute.eventTopic(), pipelineRoute.partition(), event.id(), body));
  }

  private Flux<Event> buildStates() {
    return stream.size(pipelineRoute.eventTopic(), pipelineRoute.partition())
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
                 .log(LOGGER, Level.INFO, pipelineLogConfig.showLine(), pipelineLogConfig.signalTypeArray());
  }

  private Flux<Event> readEvents(long until) {
    if (until > 0)
      return stream.subscribe(pipelineRoute.commandTopic(), pipelineRoute.partition(), 0)
                   .take(until)
                   .concatMap(this::toEvent)
                   .log(LOGGER, Level.INFO, pipelineLogConfig.showLine(), pipelineLogConfig.signalTypeArray());
    else
      return Flux.empty();
  }

  private Flux<Command> streamCommands() {
    return stream.subscribe(pipelineRoute.commandTopic(), pipelineRoute.partition(), 0)
                 .concatMap(this::toCommand)
                 .filter(cmd -> !processedCmds.contains(cmd.id()))
                 .log(LOGGER, Level.INFO, pipelineLogConfig.showLine(), pipelineLogConfig.signalTypeArray());
  }
}
