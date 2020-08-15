package io.memoria.jutils.core.eventsourcing.event;

import reactor.core.publisher.Mono;

public interface EventWriteRepo<E extends Event> {
  Mono<Void> add(String streamId, E event);
}
