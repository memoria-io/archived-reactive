package io.memoria.jutils.core.adapter.crud.memory;

import io.memoria.jutils.core.domain.Err.AlreadyExists;
import io.memoria.jutils.core.domain.port.crud.WriteRepo;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

import static io.memoria.jutils.core.domain.Err.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.domain.Err.NotFound.NOT_FOUND;

public class MemoryWriteRepo<K, V> implements WriteRepo<K, V> {
  public final Map<K, V> db;

  public MemoryWriteRepo(Map<K, V> db) {
    this.db = db;
  }

  @Override
  public Mono<Void> create(K k, V v) {
    return Mono.fromCallable(() -> db.containsKey(k))
               .flatMap(exists -> (exists) ? Mono.error(ALREADY_EXISTS) : Mono.fromRunnable(() -> db.put(k, v)));
  }

  @Override
  public Mono<Void> delete(K id) {
    return Mono.fromRunnable(() -> db.remove(id));
  }

  @Override
  public Mono<Void> update(K k, V v) {
    return Mono.fromCallable(() -> db.containsKey(k))
               .flatMap(exists -> (exists) ? Mono.fromRunnable(() -> db.put(k, v)) : Mono.error(NOT_FOUND));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MemoryWriteRepo<?, ?> that = (MemoryWriteRepo<?, ?>) o;
    return db.equals(that.db);
  }

  @Override
  public int hashCode() {
    return Objects.hash(db);
  }
}
