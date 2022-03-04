package io.memoria.reactive.core.eventsourcing.saga;

import io.memoria.reactive.core.eventsourcing.CommandStream;
import io.memoria.reactive.core.eventsourcing.EventStream;
import io.memoria.reactive.core.eventsourcing.PipelineConfig;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import reactor.core.publisher.Flux;
import reactor.util.Logger;
import reactor.util.Loggers;

public class SagaPipeline {
  private static final Logger LOGGER = Loggers.getLogger(SagaPipeline.class.getName());
  private final PipelineConfig config;
  private final EventStream eventStream;
  private final CommandStream commandStream;
  private final SagaDecider sagaDecider;

  public SagaPipeline(EventStream eventStream, CommandStream commandStream, SagaDecider sagaDecider) {
    this(PipelineConfig.DEFAULT, eventStream, commandStream, sagaDecider);
  }

  public SagaPipeline(PipelineConfig config,
                      EventStream eventStream,
                      CommandStream commandStream,
                      SagaDecider sagaDecider) {
    this.config = config;
    this.eventStream = eventStream;
    this.commandStream = commandStream;
    this.sagaDecider = sagaDecider;
  }

  /**
   * @param eventsOffset the events reading offset
   * @return Commands Ids flux
   */
  public Flux<Id> run(long eventsOffset) {
    var cmds = eventStream.subscribe(eventsOffset)
                          .log(LOGGER, config.logLevel(), config.showLine(), config.signalTypeArray())
                          .map(sagaDecider)
                          .log(LOGGER, config.logLevel(), config.showLine(), config.signalTypeArray())
                          .concatMap(ReactorVavrUtils::toMono);
    return commandStream.publish(cmds);
  }
}
