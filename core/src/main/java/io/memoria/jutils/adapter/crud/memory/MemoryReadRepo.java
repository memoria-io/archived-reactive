package io.memoria.jutils.adapter.crud.memory;

import io.memoria.jutils.core.crud.ReadRepo;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

public class MemoryReadRepo<K, V> implements ReadRepo<K, V> {
  public final Map<K, V> db;

  public MemoryReadRepo(Map<K, V> db) {
    this.db = db;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MemoryReadRepo<?, ?> that = (MemoryReadRepo<?, ?>) o;
    return db.equals(that.db);
  }

  @Override
  public Mono<Boolean> exists(K id) {
    return Mono.fromCallable(() -> db.containsKey(id));
  }

  @Override
  public Mono<Option<V>> get(K id) {
    return Mono.fromCallable(() -> Option.of(db.get(id)));
  }

  @Override
  public int hashCode() {
    return Objects.hash(db);
  }
}
