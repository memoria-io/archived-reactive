package io.memoria.reactive.core.eventsourcing;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface Subscriber<T> {
  Flux<T> sub(int offset);
}
