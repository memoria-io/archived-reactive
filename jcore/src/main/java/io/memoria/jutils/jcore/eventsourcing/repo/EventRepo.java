package io.memoria.jutils.jcore.eventsourcing.repo;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface EventRepo {
  Mono<Void> createTopic(String topic);

  Mono<Integer> add(String topic, List<Event> event);

  Mono<List<Event>> find(String topic);
}
