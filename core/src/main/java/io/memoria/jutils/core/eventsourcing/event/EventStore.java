package io.memoria.jutils.core.eventsourcing.event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStore<E extends Event> {
  Mono<Void> add(String streamId, E event);

  Mono<Boolean> exists(String streamId);

  Flux<E> stream(String streamId);
}
