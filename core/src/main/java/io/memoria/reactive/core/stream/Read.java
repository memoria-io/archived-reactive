package io.memoria.reactive.core.stream;

import io.vavr.collection.LinkedHashMap;
import reactor.core.publisher.Mono;

public interface Read<T> {
  Mono<LinkedHashMap<Long, T>> read(long offset);
}
