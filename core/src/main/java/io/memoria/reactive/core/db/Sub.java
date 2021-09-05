package io.memoria.reactive.core.db;

import reactor.core.publisher.Flux;

public interface Sub<T> {
  Flux<Msg<T>> subscribe(int offset);
}
