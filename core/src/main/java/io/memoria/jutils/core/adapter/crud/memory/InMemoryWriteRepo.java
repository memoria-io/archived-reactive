package io.memoria.jutils.core.adapter.crud.memory;

import io.memoria.jutils.core.domain.port.crud.Storable;
import io.memoria.jutils.core.domain.port.crud.WriteRepo;
import reactor.core.publisher.Mono;

import java.util.Map;

import static io.memoria.jutils.core.domain.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.domain.NotFound.NOT_FOUND;

public class InMemoryWriteRepo<K, V extends Storable<K>> implements WriteRepo<K, V> {
  protected final Map<K, V> db;

  public InMemoryWriteRepo(Map<K, V> db) {
    this.db = db;
  }

  @Override
  public Mono<V> create(V v) {
    if (db.containsKey(v.id())) {
      return Mono.error(ALREADY_EXISTS);
    }
    db.put(v.id(), v);
    return Mono.justOrEmpty(v);
  }

  @Override
  public Mono<V> update(V v) {
    if (db.containsKey(v.id())) {
      db.put(v.id(), v);
      return Mono.just(v);
    } else {
      return Mono.error(NOT_FOUND);
    }
  }

  @Override
  public Mono<Void> delete(K id) {
    db.remove(id);
    return Mono.empty();
  }
}
