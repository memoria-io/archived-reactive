package io.memoria.jutils.core.crud;

import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public interface ReadRepo<K, V> {
  Mono<Boolean> exists(K k);

  Mono<Option<V>> get(K k);
}
