package io.memoria.jutils.core.eventsourcing.event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStore {
  Mono<Void> add(String streamId, Event event);

  Mono<Void> add(String streamId, Iterable<Event> events);

  Mono<Boolean> exists(String streamId);

  Flux<Event> stream(String streamId);
}
