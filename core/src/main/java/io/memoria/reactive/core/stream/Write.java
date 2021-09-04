package io.memoria.reactive.core.stream;

import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface Write<T> {
  Mono<List<Long>> write(List<T> events);
}
