package io.memoria.reactive.core.eventsourcing.saga;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.LogConfig;
import io.memoria.reactive.core.eventsourcing.StreamConfig;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.text.TextTransformer;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.logging.Level;

public class SagaPipeline {
  private static final Logger LOGGER = Loggers.getLogger(SagaPipeline.class.getName());
  // Infra
  private final Stream stream;
  private final TextTransformer transformer;
  // Business logic
  private final SagaDecider sagaDecider;
  // Config
  private final StreamConfig commandConfig;
  private final StreamConfig eventConfig;
  private final LogConfig logConfig;

  public SagaPipeline(Stream stream,
                      TextTransformer transformer,
                      SagaDecider sagaDecider,
                      StreamConfig commandConfig,
                      StreamConfig eventConfig,
                      LogConfig logConfig) {
    // Infra
    this.stream = stream;
    this.transformer = transformer;
    // Business logic
    this.sagaDecider = sagaDecider;
    // Config
    this.commandConfig = commandConfig;
    this.eventConfig = eventConfig;
    this.logConfig = logConfig;
  }

  public Flux<Command> run() {
    var cmds = streamEvents().map(sagaDecider).concatMap(ReactorVavrUtils::toMono);
    return publishCommands(cmds);
  }

  public Mono<Command> toCommand(Msg msg) {
    return transformer.deserialize(msg.value(), Command.class);
  }

  public Mono<Event> toEvent(Msg msg) {
    return transformer.deserialize(msg.value(), Event.class);
  }

  public Mono<Msg> toMsg(Command command) {
    return transformer.serialize(command).map(body -> {
      var partition = Math.abs(command.stateId().hashCode()) % commandConfig.totalPartitions();
      return new Msg(commandConfig.topic(), partition, command.id(), body);
    });
  }

  private Flux<Command> publishCommands(Flux<Command> commands) {
    var msgs = commands.concatMap(this::toMsg);
    return stream.publish(msgs)
                 .concatMap(this::toCommand)
                 .log(LOGGER, Level.INFO, logConfig.showLine(), logConfig.signalTypeArray());
  }

  private Flux<Event> streamEvents() {
    return stream.subscribe(eventConfig.topic(), eventConfig.partition(), eventConfig.offset())
                 .concatMap(this::toEvent)
                 .log(LOGGER, Level.INFO, logConfig.showLine(), logConfig.signalTypeArray());
  }
}
