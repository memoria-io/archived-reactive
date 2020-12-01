package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.value.Id;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

public interface EventStore {
  Mono<Event> add(Id id, Event event);

  <V> Mono<V> apply(Id id, Callable<V> action);

  void endTransaction(Id id);

  Mono<List<Event>> get(Id id);

  void startTransaction(Id id);
}
