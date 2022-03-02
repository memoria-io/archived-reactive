package io.memoria.reactive.core.eventsourcing.saga;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandStream;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventStream;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

  public Flux<Command> run(long eventsOffset) {
    return eventStream.subscribe(eventsOffset).concatMap(this::apply);
  }

  private Mono<Command> apply(Event event) {
    return ReactorVavrUtils.toMono(sagaDecider.apply(event)).flatMap(commandStream::publish);
  }
}
