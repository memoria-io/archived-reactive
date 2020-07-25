package io.memoria.jutils.core.eventsourcing.event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepo<K, E extends Event> {
  Flux<E> stream(K k);

  Mono<Boolean> exists(K k);

  Mono<Void> add(K k, E e);
}
