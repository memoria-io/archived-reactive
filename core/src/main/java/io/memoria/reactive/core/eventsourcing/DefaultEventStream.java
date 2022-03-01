package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.stream.OStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

record DefaultEventStream(String topic, OStreamRepo oStreamRepo, TextTransformer transformer)
        implements EventStream {
  @Override
  public Mono<String> createTopic() {
    return oStreamRepo.create(topic);
  }

  @Override
  public Mono<Event> publish(Event event) {
    return EventStream.toEventMsg(event, transformer)
                      .flatMap(msg -> oStreamRepo.publish(topic, msg))
                      .thenReturn(event);
  }

  @Override
  public Mono<Long> size() {
    return oStreamRepo.size(topic);
  }

  @Override
  public Flux<Event> subscribe(long skipped) {
    return oStreamRepo.subscribe(topic, skipped).flatMap(msg -> EventStream.toEvent(msg, transformer));
  }
}
