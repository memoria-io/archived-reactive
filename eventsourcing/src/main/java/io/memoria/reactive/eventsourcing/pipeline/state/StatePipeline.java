package io.memoria.reactive.eventsourcing.pipeline.state;

import io.memoria.atom.core.text.TextTransformer;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import io.memoria.reactive.eventsourcing.Command;
import io.memoria.reactive.eventsourcing.CommandId;
import io.memoria.reactive.eventsourcing.Event;
import io.memoria.reactive.eventsourcing.EventId;
import io.memoria.reactive.eventsourcing.pipeline.LogConfig;
import io.memoria.reactive.eventsourcing.State;
import io.memoria.reactive.eventsourcing.StateId;
import io.memoria.reactive.eventsourcing.pipeline.Route;
import io.memoria.reactive.eventsourcing.repo.Msg;
import io.memoria.reactive.eventsourcing.repo.Stream;
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

import static io.memoria.reactive.core.vavr.ReactorVavrUtils.toMono;

public class StatePipeline<S extends State, C extends Command, E extends Event> {
  private static final Logger LOGGER = Loggers.getLogger(StatePipeline.class.getName());
  // Domain logic
  private final StateDomain<S, C, E> stateDomain;
  private final Stream stream;
  // Infra
  private final TextTransformer transformer;
  private final Route route;
  // Configs
  private final LogConfig logConfig;
  // State
  private final Map<StateId, S> stateRepo;
  private final Set<StateId> reducedStates;
  private final Set<EventId> processedEvents;
  private final Set<CommandId> processedCmds;

  public StatePipeline(StateDomain<S, C, E> stateDomain,
                       Stream stream,
                       TextTransformer transformer,
                       Route route,
                       LogConfig logConfig) {
    // Domain logic
    this.stateDomain = stateDomain;
    // Infra
    this.stream = stream;
    this.transformer = transformer;
    this.route = route;
    // Config
    this.logConfig = logConfig;
    // State
    this.stateRepo = new ConcurrentHashMap<>();
    this.reducedStates = new HashSet<>();
    this.processedEvents = new HashSet<>();
    this.processedCmds = new HashSet<>();
  }

  public Flux<E> run() {
    var readCurrentSink = read(route.newEventTopic(), route.partition()).doOnNext(this::evolveState);
    var pubOldSinkEvents = publishEvents(readOldSink()).doOnNext(this::evolveState);
    var pubCmdEvents = publishEvents(handleNewCommands()).doOnNext(this::evolveState);
    return readCurrentSink.concatWith(pubOldSinkEvents).concatWith(pubCmdEvents);
  }

  public Flux<E> runReduced() {
    var publishReduced = publishEvents(reducedEvents());
    var pubCmdEvents = publishEvents(handleNewCommands()).doOnNext(this::evolveState);
    return publishReduced.concatWith(pubCmdEvents);
  }

  public Mono<E> toEvent(Msg msg) {
    return toMono(transformer.deserialize(msg.value(), stateDomain.eventClass()));
  }

  public S stateOrInit(StateId stateId) {
    return Option.of(stateRepo.get(stateId)).getOrElse(stateDomain.initState());
  }

  public Mono<Msg> toMsg(Event event) {
    return toMono(transformer.serialize(event))
                      .map(body -> new Msg(route.newEventTopic(), route.partition(), event.eventId(), body));
  }

  public Mono<C> toCommand(Msg msg) {
    return toMono(transformer.deserialize(msg.value(), stateDomain.commandClass()));
  }

  private Flux<E> reducedEvents() {
    var compacted = read(route.newEventTopic(), route.partition()).doOnNext(this::evolveState)
                                                                  .doOnNext(e -> reducedStates.add(e.stateId()));
    var nonCompacted = readOldSink().filter(e -> !reducedStates.contains(e.stateId())).doOnNext(this::evolveState);
    var compactNonCom = Flux.fromIterable(this.stateRepo.entrySet())
                            .filter(e -> !reducedStates.contains(e.getKey()))
                            .map(Entry::getValue)
                            .map(stateDomain.reducer());
    return compacted.thenMany(nonCompacted).thenMany(compactNonCom);
  }

  private Flux<E> read(String topic, int partition) {
    return stream.size(topic, partition)
                 .filter(size -> size > 0)
                 .flatMapMany(size -> stream.subscribe(topic, partition, 0).take(size))
                 .concatMap(this::toEvent)
                 .log(LOGGER, logConfig.level(), logConfig.showLine(), logConfig.signalTypeArray());
  }

  private void evolveState(E event) {
    var currentState = stateOrInit(event.stateId());
    var newState = stateDomain.evolver().apply(currentState, event);
    stateRepo.put(event.stateId(), newState);
    processedCmds.add(event.commandId());
    processedEvents.add(event.eventId());
  }

  private Flux<E> publishEvents(Flux<E> events) {
    return stream.publish(events.concatMap(this::toMsg))
                 .concatMap(this::toEvent)
                 .log(LOGGER, logConfig.level(), logConfig.showLine(), logConfig.signalTypeArray());
  }

  private Flux<E> readOldSink() {
    return readAll(route.oldEventTopic(), route.oldPartitions()).filter(this::isEligible)
                                                                .sequential()
                                                                .publishOn(Schedulers.single());
  }

  private Flux<E> handleNewCommands() {
    return stream.subscribe(route.commandTopic(), route.partition(), 0)
                 .concatMap(this::toCommand)
                 .concatMap(this::rerouteIfNotEligible)
                 .filter(this::isEligible)
                 .filter(cmd -> !processedCmds.contains(cmd.commandId()))
                 .log(LOGGER, logConfig.level(), logConfig.showLine(), logConfig.signalTypeArray())
                 .map(this::decide)
                 .concatMap(ReactorVavrUtils::toMono);
  }

  private ParallelFlux<E> readAll(String prevTopic, int prevTotal) {
    if (prevTotal > 0)
      return Flux.range(0, prevTotal).parallel(prevTotal).runOn(Schedulers.parallel()).flatMap(i -> read(prevTopic, i));
    else
      return ParallelFlux.from(Flux.empty());
  }

  private Flux<C> rerouteIfNotEligible(C cmd) {
    if (isEligible(cmd)) {
      return Flux.just(cmd);
    } else {
      var msg = transformer.serialize(cmd).map(body -> rerouteCommand(cmd, body));
      var msgMono = toMono(msg);
      return stream.publish(msgMono.flux()).thenMany(Flux.just(cmd));
    }
  }

  private Msg rerouteCommand(C cmd, String body) {
    return new Msg(route.commandTopic(), cmd.partition(route.newPartitions()), cmd.commandId(), body);
  }

  private boolean isEligible(C cmd) {
    return cmd.isInPartition(route.partition(), route.newPartitions());
  }

  private boolean isEligible(E e) {
    return e.isInPartition(route.partition(), route.newPartitions()) && !processedEvents.contains(e.eventId());
  }

  private Try<E> decide(C cmd) {
    var state = stateOrInit(cmd.stateId());
    return stateDomain.decider().apply(state, cmd);
  }
}