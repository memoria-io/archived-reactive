package io.memoria.reactive.core.db;

import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface Write<T> {
  Mono<List<T>> write(List<T> msgs);
}
