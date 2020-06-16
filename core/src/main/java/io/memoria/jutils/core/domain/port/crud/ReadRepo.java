package io.memoria.jutils.core.domain.port.crud;

import io.vavr.control.Try;
import reactor.core.publisher.Mono;

public interface ReadRepo<T extends Storable> {
  Mono<Try<T>> get(String id);

  Mono<Try<Boolean>> exists(String id);
}
