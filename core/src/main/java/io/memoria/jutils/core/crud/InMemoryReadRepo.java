package io.memoria.jutils.core.crud;

import io.vavr.control.Option;
import reactor.core.publisher.Mono;

import java.util.Map;

public class InMemoryReadRepo<K, V> implements ReadRepo<K, V> {
  private final Map<K, V> db;

  public InMemoryReadRepo(Map<K, V> db) {
    this.db = db;
  }

  @Override
  public Mono<Boolean> exists(K id) {
    return Mono.fromCallable(() -> db.containsKey(id));
  }

  @Override
  public Mono<Option<V>> get(K id) {
    return Mono.fromCallable(() -> Option.of(db.get(id)));
  }
}