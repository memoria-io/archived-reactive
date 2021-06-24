package io.memoria.jutils.jcore.eventsourcing.repo;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface EventRepo {
  Mono<Void> add(String aggregate, List<Event> event);

  Mono<List<Event>> find(String aggregate);
}
