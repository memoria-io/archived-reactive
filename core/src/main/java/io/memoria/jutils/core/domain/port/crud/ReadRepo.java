package io.memoria.jutils.core.domain.port.crud;

import reactor.core.publisher.Mono;

public interface ReadRepo<K, V extends Storable<K>> {
  Mono<V> get(K k);

  Mono<Boolean> exists(K k);
}
