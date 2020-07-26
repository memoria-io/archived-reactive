package io.memoria.jutils.core.eventsourcing.event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepo<K, E extends Event> {
  Mono<Void> add(K k, E e);

  Mono<Boolean> exists(K k);

  Flux<E> stream(K k);
}
