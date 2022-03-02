package io.memoria.reactive.core.eventsourcing.saga;

import io.memoria.reactive.core.eventsourcing.CommandStream;
import io.memoria.reactive.core.eventsourcing.EventStream;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import reactor.core.publisher.Flux;

@SuppressWarnings("ClassCanBeRecord")
public class SagaPipeline {
  private final EventStream eventStream;
  private final CommandStream commandStream;
  private final SagaDecider sagaDecider;

  public SagaPipeline(EventStream eventStream, CommandStream commandStream, SagaDecider sagaDecider) {
    this.eventStream = eventStream;
    this.commandStream = commandStream;
    this.sagaDecider = sagaDecider;
  }

  /**
   * @param eventsOffset the events reading offset
   * @return Commands Ids flux
   */
  public Flux<Id> run(long eventsOffset) {
    var cmds = eventStream.subscribe(eventsOffset).map(sagaDecider).concatMap(ReactorVavrUtils::toMono);
    return commandStream.publish(cmds);
  }
}
