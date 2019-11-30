package com.marmoush.jutils.domain.entity;

import io.vavr.control.Try;
import reactor.core.publisher.Mono;

public interface EntityWriteRepo<T extends Entity<?>> {
  Mono<Try<T>> create(T t);

  Mono<Try<T>> update(T t);

  Mono<Void> delete(String id);
}
