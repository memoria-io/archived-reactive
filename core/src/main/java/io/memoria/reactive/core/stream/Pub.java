package io.memoria.reactive.core.stream;

import reactor.core.publisher.Flux;

public interface Pub<T> {
  Flux<Long> publish(Flux<T> msgs);
}
