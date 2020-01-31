package com.marmoush.jutils.core.domain.port.crud;

import com.marmoush.jutils.core.domain.entity.Entity;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

public interface EntityReadRepo<T extends Entity<?>> {
  Mono<Try<T>> get(String id);

  Mono<Try<Void>> exists(String id);
}
