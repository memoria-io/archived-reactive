package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.text.TextTransformer;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StatePipeline {
  private static final Logger LOGGER = Loggers.getLogger(StatePipeline.class.getName());
  // Infra
  private final Stream stream;
  private final TextTransformer transformer;
  private final Set<Id> processedEvents;
  private final Set<Id> processedCmds;
  private final Set<StateId> compactedStates;
  private final Map<StateId, State> stateRepo;
  // Business logic
  private final State initState;
  private final StateDecider decider;
  private final StateEvolver evolver;
  private final StateCompactor compactor;
  // Configs
  private final Route route;
  private final LogConfig logConfig;

  public StatePipeline(Stream stream,
                       TextTransformer transformer,
                       State initState,
                       StateDecider decider,
                       StateEvolver evolver,
                       StateCompactor compactor,
                       Route route,
                       LogConfig logConfig) {
    // Infra
    this.stream = stream;
    this.transformer = transformer;
    this.processedEvents = new HashSet<>();
    this.processedCmds = new HashSet<>();
    this.compactedStates = new HashSet<>();
    this.stateRepo = new ConcurrentHashMap<>();
    // Business logic
    this.initState = initState;
    this.decider = decider;
    this.evolver = evolver;
    this.compactor = compactor;
    // Configs
    this.route = route;
    this.logConfig = logConfig;
  }

  public Flux<Event> run() {
    var readCurrentSink = read(route.eventTopic(), route.partition()).doOnNext(this::evolveState);
    var pubOldSinkEvents = publishEvents(readOldSink()).doOnNext(this::evolveState);
    var pubCmdEvents = publishEvents(handleNewCommands()).doOnNext(this::evolveState);
    return readCurrentSink.concatWith(pubOldSinkEvents).concatWith(pubCmdEvents);
  }

  public Flux<Event> compactRun() {
    var publishCompaction = publishEvents(compactionEvents());
    var pubCmdEvents = publishEvents(handleNewCommands()).doOnNext(this::evolveState);
    return publishCompaction.concatWith(pubCmdEvents);
  }

  public Mono<Event> toEvent(Msg msg) {
    return transformer.deserialize(msg.value(), Event.class);
  }

  public State stateOrInit(StateId stateId) {
    return Option.of(stateRepo.get(stateId)).getOrElse(initState);
  }

  public Mono<Msg> toMsg(Event event) {
    return transformer.serialize(event)
                      .map(body -> new Msg(route.eventTopic(), route.partition(), event.eventId(), body));
  }

  public Mono<Command> toCommand(Msg msg) {
    return transformer.deserialize(msg.value(), Command.class);
  }

  private Flux<Event> compactionEvents() {
    var compacted = read(route.eventTopic(), route.partition()).doOnNext(this::evolveState)
                                                               .doOnNext(e -> compactedStates.add(e.stateId()));
    var nonCompacted = readOldSink().filter(e -> !compactedStates.contains(e.stateId())).doOnNext(this::evolveState);
    var compactNonCom = Flux.fromIterable(this.stateRepo.entrySet())
                            .filter(e -> !compactedStates.contains(e.getKey()))
                            .map(Entry::getValue)
                            .map(compactor);
    return compacted.thenMany(nonCompacted).thenMany(compactNonCom);
  }

  private Flux<Event> read(String topic, int partition) {
    return stream.size(topic, partition)
                 .filter(size -> size > 0)
                 .flatMapMany(size -> stream.subscribe(topic, partition, 0).take(size))
                 .concatMap(this::toEvent)
                 .log(LOGGER, logConfig.level(), logConfig.showLine(), logConfig.signalTypeArray());
  }

  private void evolveState(Event event) {
    var currentState = stateOrInit(event.stateId());
    var newState = evolver.apply(currentState, event);
    stateRepo.put(event.stateId(), newState);
    processedCmds.add(event.commandId());
    processedEvents.add(event.eventId());
  }

  private Flux<Event> publishEvents(Flux<Event> events) {
    return stream.publish(events.concatMap(this::toMsg))
                 .concatMap(this::toEvent)
                 .log(LOGGER, logConfig.level(), logConfig.showLine(), logConfig.signalTypeArray());
  }

  private Flux<Event> readOldSink() {
    return readAll(route.prevEventTopic(), route.prevPartitions()).filter(this::isEligible)
                                                                  .sequential()
                                                                  .publishOn(Schedulers.single());
  }

  private Flux<Event> handleNewCommands() {
    return stream.subscribe(route.commandTopic(), route.partition(), 0)
                 .concatMap(this::toCommand)
                 .concatMap(this::rerouteIfNotEligible)
                 .filter(this::isEligible)
                 .filter(cmd -> !processedCmds.contains(cmd.commandId()))
                 .log(LOGGER, logConfig.level(), logConfig.showLine(), logConfig.signalTypeArray())
                 .map(this::decide)
                 .concatMap(ReactorVavrUtils::toMono);
  }

  private ParallelFlux<Event> readAll(String prevTopic, int prevTotal) {
    if (prevTotal > 0)
      return Flux.range(0, prevTotal).parallel(prevTotal).runOn(Schedulers.parallel()).flatMap(i -> read(prevTopic, i));
    else
      return ParallelFlux.from(Flux.empty());
  }

  private Flux<Command> rerouteIfNotEligible(Command cmd) {
    if (isEligible(cmd)) {
      return Flux.just(cmd);
    } else {
      var msg = transformer.serialize(cmd).map(body -> rerouteCommand(cmd, body));
      return stream.publish(msg.flux()).thenMany(Flux.just(cmd));
    }
  }

  private Msg rerouteCommand(Command cmd, String body) {
    return new Msg(route.commandTopic(), cmd.partition(route.totalPartitions()), cmd.commandId(), body);
  }

  private boolean isEligible(Command cmd) {
    return cmd.isInPartition(route.partition(), route.totalPartitions());
  }

  private boolean isEligible(Event e) {
    return e.isInPartition(route.partition(), route.totalPartitions()) && !processedEvents.contains(e.eventId());
  }

  private Try<Event> decide(Command cmd) {
    var state = stateOrInit(cmd.stateId());
    return decider.apply(state, cmd);
  }
}
