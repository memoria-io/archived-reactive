package io.memoria.reactive.core.eventsourcing.saga;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandStream;
import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import io.vavr.Function1;
import reactor.core.publisher.Mono;

public class SagaPipeline implements Function1<Event, Mono<Command>> {
  private final CommandStream commandStream;
  private final SagaDecider sagaDecider;

  public SagaPipeline(CommandStream commandStream, SagaDecider sagaDecider) {
    this.commandStream = commandStream;
    this.sagaDecider = sagaDecider;
  }

  public Mono<Command> apply(Event event) {
    return ReactorVavrUtils.toMono(sagaDecider.apply(event)).flatMap(commandStream::publish);
  }
}
