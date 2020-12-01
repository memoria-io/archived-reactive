package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.value.Id;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

public interface EventStore {
  Mono<Event> add(Id id, Event event);

  Mono<List<Event>> get(Id id);

  <V> Mono<V> transaction(Id id, Callable<V> action);
}
