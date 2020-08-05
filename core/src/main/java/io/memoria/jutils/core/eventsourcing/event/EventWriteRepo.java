package io.memoria.jutils.core.eventsourcing.event;

import reactor.core.publisher.Mono;

public interface EventWriteRepo<K, E extends Event> {
  Mono<Void> add(K k, E e);
}
