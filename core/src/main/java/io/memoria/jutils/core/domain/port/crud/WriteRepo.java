package io.memoria.jutils.core.domain.port.crud;

import io.vavr.control.Try;
import reactor.core.publisher.Mono;

public interface WriteRepo<T extends Storable> {
  Mono<Try<T>> create(T t);

  Mono<Try<T>> update(T t);

  Mono<Void> delete(String id);
}
