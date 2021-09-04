package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;
import io.vavr.Tuple2;
import reactor.core.publisher.Flux;

public interface Sub<T> {
  Flux<Tuple2<Id, T>> subscribe(long offset);
}
