package io.memoria.reactive.core.stream;

import reactor.core.publisher.Flux;

public interface Sub<T> {
  Flux<T> subscribe(long offset);
}
