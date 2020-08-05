package io.memoria.jutils.core.eventsourcing.event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventReadRepo<K, E extends Event> {
  Mono<Boolean> exists(K k);

  Mono<E> first(K k, String aggregateId);

  Flux<E> stream(K k);

  Flux<E> filter(K k, String aggregateId);
}
