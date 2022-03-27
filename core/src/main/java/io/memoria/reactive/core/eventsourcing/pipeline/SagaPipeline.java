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

import java.util.logging.Level;

@SuppressWarnings("ClassCanBeRecord")
public class SagaPipeline {
  private static final Logger LOGGER = Loggers.getLogger(SagaPipeline.class.getName());
  // Infra
  private final Stream stream;
  private final TextTransformer transformer;
  // Business logic
  private final SagaDecider sagaDecider;
  // Config
  private final PipelineRoute pipelineRoute;
  private final PipelineLogConfig pipelineLogConfig;

  public SagaPipeline(Stream stream,
                      TextTransformer transformer,
                      SagaDecider sagaDecider,
                      PipelineRoute pipelineRoute,
                      PipelineLogConfig pipelineLogConfig) {
    // Infra
    this.stream = stream;
    this.transformer = transformer;
    // Business logic
    this.sagaDecider = sagaDecider;
    // Config
    this.pipelineRoute = pipelineRoute;
    this.pipelineLogConfig = pipelineLogConfig;
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
      var partition = Math.abs(command.stateId().hashCode()) % pipelineRoute.totalPartitions();
      return new Msg(pipelineRoute.commandTopic(), partition, command.id(), body);
    });
  }

  private Flux<Command> publishCommands(Flux<Command> commands) {
    var msgs = commands.concatMap(this::toMsg);
    return stream.publish(msgs)
                 .concatMap(this::toCommand)
                 .log(LOGGER, Level.INFO, pipelineLogConfig.showLine(), pipelineLogConfig.signalTypeArray());
  }

  private Flux<Event> streamEvents() {
    return stream.subscribe(pipelineRoute.eventTopic(), pipelineRoute.partition(), 0)
                 .concatMap(this::toEvent)
                 .log(LOGGER, Level.INFO, pipelineLogConfig.showLine(), pipelineLogConfig.signalTypeArray());
  }
}
