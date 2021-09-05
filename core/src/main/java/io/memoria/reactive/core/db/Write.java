package io.memoria.reactive.core.db;

import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface Write<T> {
  Mono<List<Long>> write(List<Msg<T>> msgs);
}
