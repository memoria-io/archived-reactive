package io.memoria.reactive.core.eventsourcing.pipeline;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.text.TextTransformer;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@SuppressWarnings("ClassCanBeRecord")
public class SagaPipeline {
  private static final Logger LOGGER = Loggers.getLogger(SagaPipeline.class.getName());
  // Infra
  private final Stream stream;
  private final TextTransformer transformer;
  // Business logic
  private final SagaDecider sagaDecider;
  // Config
  private final Route route;
  private final LogConfig logConfig;

  public SagaPipeline(Stream stream,
                      TextTransformer transformer,
                      SagaDecider sagaDecider,
                      Route route,
                      LogConfig logConfig) {
    // Infra
    this.stream = stream;
    this.transformer = transformer;
    // Business logic
    this.sagaDecider = sagaDecider;
    // Config
    this.route = route;
    this.logConfig = logConfig;
  }

  public Flux<Command> run() {
    var cmds = streamEvents().map(sagaDecider).concatMap(ReactorVavrUtils::toMono);
    return publishCommands(cmds);
  }

  public Mono<Event> toEvent(Msg msg) {
    return transformer.deserialize(msg.value(), Event.class);
  }

  public Mono<Msg> toMsg(Command command) {
    return transformer.serialize(command).map(body -> {
      var partition = command.partition(route.totalPartitions());
      return new Msg(route.commandTopic(), partition, command.commandId(), body);
    });
  }

  public Mono<Command> toCommand(Msg msg) {
    return transformer.deserialize(msg.value(), Command.class);
  }

  private Flux<Event> streamEvents() {
    return stream.subscribe(route.eventTopic(), route.partition(), 0).concatMap(this::toEvent);
  }

  private Flux<Command> publishCommands(Flux<Command> commands) {
    var msgs = commands.concatMap(this::toMsg);
    return stream.publish(msgs)
                 .concatMap(this::toCommand)
                 .log(LOGGER, logConfig.level(), logConfig.showLine(), logConfig.signalTypeArray());
  }
}
