package io.memoria.jutils.core.domain.port.crud;

import reactor.core.publisher.Mono;

public interface ReadRepo<K, V extends Storable<K>> {
  Mono<Boolean> exists(K k);

  Mono<V> get(K k);
}
