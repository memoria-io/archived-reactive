package io.memoria.reactive.core.db;

import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface Read<T extends Msg> {
  Mono<Long> currentIndex();

  Mono<List<T>> read(int offset);

  Mono<Integer> size();
}
