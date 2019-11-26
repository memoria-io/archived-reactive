package com.marmoush.jutils.domain.entity;

import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

public interface EntityRepo<T extends Entity<?>> {
  Mono<Try<T>> create(T t);

  Mono<Option<T>> get(String id);

  Mono<Void> delete(String id);
}
