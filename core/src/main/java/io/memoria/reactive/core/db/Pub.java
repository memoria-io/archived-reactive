package io.memoria.reactive.core.db;

import reactor.core.publisher.Flux;

public interface Pub<T> {
  Flux<T> publish(Flux<T> msgs);
}
