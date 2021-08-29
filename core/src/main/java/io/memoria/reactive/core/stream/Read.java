package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;
import io.vavr.collection.LinkedHashMap;
import reactor.core.publisher.Mono;

public interface Read<T> {
  Mono<LinkedHashMap<Id, T>> read(long offset);
}
