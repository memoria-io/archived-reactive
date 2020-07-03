package io.memoria.jutils.core.adapter.crud.memory;

import io.memoria.jutils.core.domain.port.crud.ReadRepo;
import io.memoria.jutils.core.domain.port.crud.Storable;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

import java.util.Map;

public class InMemoryReadRepo<K, V extends Storable<K>> implements ReadRepo<K, V> {
  protected final Map<K, V> db;

  public InMemoryReadRepo(Map<K, V> db) {
    this.db = db;
  }

  @Override
  public Mono<Boolean> exists(K id) {
    return Mono.just(db.containsKey(id));
  }

  @Override
  public Mono<Option<V>> get(K id) {
    return Mono.just(Option.of(db.get(id)));
  }
}