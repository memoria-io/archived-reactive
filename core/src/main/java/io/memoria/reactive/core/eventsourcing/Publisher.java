package io.memoria.reactive.core.eventsourcing;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Publisher {
  Mono<Event> publish(Event event);
}
