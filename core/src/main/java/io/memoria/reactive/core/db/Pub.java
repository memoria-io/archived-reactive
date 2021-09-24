package io.memoria.reactive.core.db;

import reactor.core.publisher.Mono;

public interface Pub<T> {
  Mono<T> publish(T msg);
}
