package io.memoria.jutils.core.domain.port.crud;

import reactor.core.publisher.Mono;

public interface WriteRepo<K, V extends Storable<K>> {
  Mono<V> create(V v);

  Mono<Void> delete(K k);

  Mono<Void> update(V v);
}
