package com.marmoush.jutils.general.domain.port.crud;

import com.marmoush.jutils.general.domain.entity.Entity;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

public interface EntityWriteRepo<T extends Entity<?>> {
  Mono<Try<T>> create(T t);

  Mono<Try<T>> update(T t);

  Mono<Void> delete(String id);
}
