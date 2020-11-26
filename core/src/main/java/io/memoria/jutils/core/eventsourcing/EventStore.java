package io.memoria.jutils.core.eventsourcing;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStore {
  Flux<Event> add(String topic, Flux<Event> events);

  Mono<Boolean> exists(String topic);

  Flux<Event> stream(String topic);
}
