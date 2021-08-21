package io.memoria.reactive.core.eventsourcing.repo;

import io.memoria.reactive.core.eventsourcing.Event;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface EventWriteRepo {
  Mono<List<Event>> add(List<Event> events);
}
