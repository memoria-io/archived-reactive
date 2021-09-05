package io.memoria.reactive.core.db;

import reactor.core.publisher.Flux;

public interface Pub<T> {
  Flux<Long> publish(Flux<Msg<T>> msgs);
}
