package io.memoria.reactive.core.stream;

import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface Read<T> {
  Mono<List<T>> read(long offset);
}
