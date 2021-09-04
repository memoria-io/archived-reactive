package io.memoria.reactive.core.stream;

import io.vavr.Tuple2;
import reactor.core.publisher.Flux;

public interface Sub<T> {
  Flux<Tuple2<Long, T>> subscribe(long offset);
}
