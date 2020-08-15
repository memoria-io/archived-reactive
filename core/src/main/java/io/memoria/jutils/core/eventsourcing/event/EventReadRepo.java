package io.memoria.jutils.core.eventsourcing.event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventReadRepo<E extends Event> {
  Mono<Boolean> exists(String streamId);

  Flux<E> stream(String streamId);
}
