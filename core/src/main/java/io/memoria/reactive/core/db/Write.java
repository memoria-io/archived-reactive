package io.memoria.reactive.core.db;

import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface Write<T extends Msg> {
  Mono<List<T>> write(List<T> msgs);
}
