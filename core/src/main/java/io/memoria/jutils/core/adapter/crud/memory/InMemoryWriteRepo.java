package io.memoria.jutils.core.adapter.crud.memory;

import io.memoria.jutils.core.domain.port.crud.WriteRepo;
import reactor.core.publisher.Mono;

import java.util.Map;

import static io.memoria.jutils.core.domain.Err.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.domain.Err.NotFound.NOT_FOUND;

public class InMemoryWriteRepo<K, V> implements WriteRepo<K, V> {
  protected final Map<K, V> db;

  public InMemoryWriteRepo(Map<K, V> db) {
    this.db = db;
  }

  @Override
  public Mono<V> create(K k, V v) {
    if (db.containsKey(k)) {
      return Mono.error(ALREADY_EXISTS);
    }
    db.put(k, v);
    return Mono.justOrEmpty(v);
  }

  @Override
  public Mono<Void> delete(K id) {
    db.remove(id);
    return Mono.empty();
  }

  @Override
  public Mono<Void> update(K k, V v) {
    if (db.containsKey(k)) {
      db.put(k, v);
      return Mono.empty();
    } else {
      return Mono.error(NOT_FOUND);
    }
  }
}
