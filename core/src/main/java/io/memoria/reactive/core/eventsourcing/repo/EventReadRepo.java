package io.memoria.reactive.core.eventsourcing.repo;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.id.Id;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface EventReadRepo {
  Mono<List<Event>> find();

  Mono<List<Event>> find(Id aggId);
}
