package io.memoria.reactive.core.rsdb;

import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface Read<T> {
  Mono<List<T>> read(int offset);
}
