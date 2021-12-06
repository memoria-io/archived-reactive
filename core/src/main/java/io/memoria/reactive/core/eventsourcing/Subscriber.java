package io.memoria.reactive.core.eventsourcing;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface Subscriber {
  Flux<Event> subscribe(long offset);
}
