package io.memoria.jutils.core.eventsourcing.stateless;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.value.Id;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EventStore {
  Mono<List<Event>> add(String topic, List<Event> events);

  Mono<List<Event>> get(String topic);

  Mono<List<Event>> get(String topic, Id id);

  Mono<Void> commit(String topic);

  Mono<Void> rollback(String topic);
}
