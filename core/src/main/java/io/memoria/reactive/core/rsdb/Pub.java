package io.memoria.reactive.core.rsdb;

import reactor.core.publisher.Mono;

public interface Pub<T> {
  Mono<T> publish(T msg);
}
