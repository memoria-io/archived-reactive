package io.memoria.reactive.core.eventsourcing.saga;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.PipelineConfig;
import io.memoria.reactive.core.eventsourcing.PipelineConfig.CommandConfig;
import io.memoria.reactive.core.eventsourcing.PipelineConfig.StreamConfig;
import io.memoria.reactive.core.eventsourcing.PipelineConfig.LogConfig;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.Msg;
import io.memoria.reactive.core.stream.Stream;
import io.memoria.reactive.core.text.TextTransformer;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

public class SagaPipeline {
  private static final Logger LOGGER = Loggers.getLogger(SagaPipeline.class.getName());
  private final Stream stream;
  private final SagaDecider sagaDecider;
  private final TextTransformer transformer;
  private final StreamConfig commandConfig;
  private final StreamConfig streamConfig;
  private final LogConfig logConfig;

  public SagaPipeline(Stream stream, SagaDecider sagaDecider, TextTransformer transformer, PipelineConfig config) {
    this.stream = stream;
    this.sagaDecider = sagaDecider;
    this.transformer = transformer;
    this.commandConfig = config.commandConfig();
    this.streamConfig = config.eventConfig();
    this.logConfig = config.logConfig();
  }

  /**
   * @param eventsOffset the events reading offset
   * @return Commands Ids flux
   */
  public Flux<Id> run() {
    var cmds = streamEvents()
                          .map(sagaDecider)
                          .log(LOGGER, logConfig.logLevel(), logConfig.showLine(), logConfig.signalTypeArray())
                          .concatMap(ReactorVavrUtils::toMono);
    return commandStream.publish(cmds);
  }

  private  Flux<Event> streamEvents() {
    stream.subscribe(streamConfig)
               .log(LOGGER, logConfig.logLevel(), logConfig.showLine(), logConfig.signalTypeArray())
  }

  private Mono<Msg> toMsg(Command command) {
    return transformer.serialize(command).map(body -> {
      var partition = Math.abs(command.stateId().hashCode()) % commandConfig.totalPartitions();
      return new Msg(commandConfig.topic(), partition, command.id(), body);
    });
  }

  private Mono<Command> toCommand(Msg msg) {
    return transformer.deserialize(msg.value(), Command.class);
  }
}
