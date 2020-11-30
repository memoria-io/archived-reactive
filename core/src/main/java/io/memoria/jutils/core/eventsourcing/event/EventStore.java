package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.value.Id;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EventStore {
  Mono<Event> add(Id topic, Event event);

  void endTransaction(Id id);

  Mono<List<Event>> get(Id topic);

  void startTransaction(Id id);
}
