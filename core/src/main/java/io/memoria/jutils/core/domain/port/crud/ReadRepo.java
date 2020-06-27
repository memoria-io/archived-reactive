package io.memoria.jutils.core.domain.port.crud;

import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public interface ReadRepo<K, V extends Storable<K>> {
  Mono<Boolean> exists(K k);

  Mono<Option<V>> get(K k);
}
