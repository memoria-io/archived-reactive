package io.memoria.reactive.core.stream;

import reactor.core.publisher.Flux;

public interface StreamRepo<T> {
  Flux<T> publish(Flux<T> msg);

  Flux<T> subscribe(long offset);
}
