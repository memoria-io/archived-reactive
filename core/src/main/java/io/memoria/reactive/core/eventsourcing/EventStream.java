package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.stream.OStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStream {
  Mono<Void> createTopic();

  static EventStream defaultEventStream(String topic, OStreamRepo oStreamRepo, TextTransformer transformer) {
    return new DefaultEventStream(topic, oStreamRepo, transformer);
  }

  Mono<Event> publish(Event event);

  Mono<Integer> size();

  Flux<Event> subscribe(int skipped);
}
