package io.memoria.jutils.core.eventsourcing.stateful;

import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.value.Id;
import reactor.core.publisher.Flux;

public interface EventStream {
  Flux<Event> publish(String topic, Flux<Event> events);

  Flux<Event> stream(String topic, Id id);

  Flux<Event> stream(String topic);
}
