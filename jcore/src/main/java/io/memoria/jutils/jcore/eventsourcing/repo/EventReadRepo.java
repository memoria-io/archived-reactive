package io.memoria.jutils.jcore.eventsourcing.repo;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface EventReadRepo {
  Mono<List<Event>> find();

  Mono<List<Event>> find(Id aggId);
}
