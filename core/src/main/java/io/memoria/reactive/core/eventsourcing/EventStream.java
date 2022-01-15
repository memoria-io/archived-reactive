package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.stream.OStreamRepo;
import io.memoria.reactive.core.text.TextTransformer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStream {
  static EventStream defaultEventStream(String topic, OStreamRepo oStreamRepo, TextTransformer transformer) {
    return new DefaultEventStream(topic, oStreamRepo, transformer);
  }

  Mono<Void> createTopic();

  Mono<Integer> size();

  Mono<Event> publish(Event event);

  Flux<Event> subscribe(int skipped);
}
