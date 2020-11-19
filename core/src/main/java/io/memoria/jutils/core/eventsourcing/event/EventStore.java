package io.memoria.jutils.core.eventsourcing.event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStore {
  Flux<String> add(String topic, Flux<Event> event);

  Mono<Boolean> exists(String topic);

  Flux<Event> stream(String topic);
}
