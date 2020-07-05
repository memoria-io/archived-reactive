package io.memoria.jutils.core.domain.port.crud;

import reactor.core.publisher.Mono;

public interface WriteRepo<K, V> {
  Mono<V> create(K k, V v);

  Mono<Void> delete(K k);

  Mono<Void> update(K k, V v);
}
