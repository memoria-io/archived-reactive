package io.memoria.reactive.core.eventsourcing.pipeline.saga;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.pipeline.LogConfig;
import io.memoria.reactive.core.eventsourcing.pipeline.Route;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.text.TextTransformer;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@SuppressWarnings("ClassCanBeRecord")
public class SagaPipeline<E extends Event, C extends Command> {
  private static final Logger LOGGER = Loggers.getLogger(SagaPipeline.class.getName());
  // Business logic
  private final SagaDomain<E, C> sagaDomain;
  // Infra
  private final Stream stream;
  private final TextTransformer transformer;
  // Config
  private final Route route;
  private final LogConfig logConfig;

  public SagaPipeline(SagaDomain<E, C> sagaDomain,
                      Stream stream,
                      TextTransformer transformer,
                      Route route,
                      LogConfig logConfig) {
    // Business logic
    this.sagaDomain = sagaDomain;
    // Infra
    this.stream = stream;
    this.transformer = transformer;
    // Config
    this.route = route;
    this.logConfig = logConfig;
  }

  public Flux<C> run() {
    var cmds = streamEvents().map(sagaDomain.decider()).concatMap(ReactorVavrUtils::toMono);
    return publishCommands(cmds);
  }

  public Mono<E> toEvent(Msg msg) {
    return transformer.deserialize(msg.value(), sagaDomain.eventClass());
  }

  public Mono<Msg> toMsg(C command) {
    return transformer.serialize(command).map(body -> {
      var partition = command.partition(route.newPartitions());
      return new Msg(route.commandTopic(), partition, command.commandId(), body);
    });
  }

  public Mono<C> toCommand(Msg msg) {
    return transformer.deserialize(msg.value(), sagaDomain.commandClass());
  }

  private Flux<E> streamEvents() {
    return stream.subscribe(route.newEventTopic(), route.partition(), 0).concatMap(this::toEvent);
  }

  private Flux<C> publishCommands(Flux<C> commands) {
    var msgs = commands.concatMap(this::toMsg);
    return stream.publish(msgs)
                 .concatMap(this::toCommand)
                 .log(LOGGER, logConfig.level(), logConfig.showLine(), logConfig.signalTypeArray());
  }
}
