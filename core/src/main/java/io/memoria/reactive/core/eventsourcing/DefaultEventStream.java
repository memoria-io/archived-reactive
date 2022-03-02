package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.stream.OStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

record DefaultEventStream(String topic, int partition, OStreamRepo oStreamRepo, TextTransformer transformer)
        implements EventStream {
  public DefaultEventStream {
    if (partition < 0)
      throw new IllegalArgumentException("Partition value can't be less than 0");
  }

  @Override
  public Mono<Event> publish(Event event) {
    return EventStream.toEventMsg(event, transformer)
                      .flatMap(msg -> oStreamRepo.publish(topic, partition, msg))
                      .thenReturn(event);
  }

  @Override
  public Mono<Long> size() {
    return oStreamRepo.size(topic, partition);
  }

  @Override
  public Flux<Event> subscribe(long skipped) {
    return oStreamRepo.subscribe(topic, partition, skipped).flatMap(msg -> EventStream.toEvent(msg, transformer));
  }
}
