package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.value.Id;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EventStore {
  Mono<Event> add(Id topic, Event event);

  Mono<List<Event>> get(Id topic);
}
