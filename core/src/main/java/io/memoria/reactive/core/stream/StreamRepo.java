package io.memoria.reactive.core.stream;

import reactor.core.publisher.Flux;

public interface StreamRepo<E> {
  Flux<E> publish(Flux<E> msg);

  Flux<E> subscribe(long offset);
}
