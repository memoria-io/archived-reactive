package io.memoria.reactive.core.eventsourcing;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Publisher<T> {
  Mono<T> pub(T msg);
}
