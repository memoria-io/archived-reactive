package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import reactor.core.publisher.Mono;

public interface Write<T> {
  Mono<Map<Id, T>> write(List<T> events);
}
