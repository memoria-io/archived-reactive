package io.memoria.jutils.core.eventsourcing;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStream {
  <E extends Event> Flux<E> add(String topic, Flux<E> Messages);

  Mono<Boolean> exists(String topic);

  <E extends Event> Flux<E> stream(String topic, Class<E> as);
}
