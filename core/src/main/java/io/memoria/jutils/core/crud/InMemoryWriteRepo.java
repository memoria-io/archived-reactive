package io.memoria.jutils.core.crud;

import reactor.core.publisher.Mono;

import java.util.Map;

import static io.memoria.jutils.core.JutilsException.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.JutilsException.NotFound.NOT_FOUND;
import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toVoidMono;

public class InMemoryWriteRepo<K, V> implements WriteRepo<K, V> {
  private final Map<K, V> db;

  public InMemoryWriteRepo(Map<K, V> db) {
    this.db = db;
  }

  @Override
  public Mono<Void> create(K k, V v) {
    return Mono.fromCallable(() -> !db.containsKey(k)).flatMap(toVoidMono(() -> db.put(k, v), ALREADY_EXISTS));
  }

  @Override
  public Mono<Void> delete(K id) {
    return Mono.fromRunnable(() -> db.remove(id));
  }

  @Override
  public Mono<Void> update(K k, V v) {
    return Mono.fromCallable(() -> db.containsKey(k)).flatMap(toVoidMono(() -> db.put(k, v), NOT_FOUND));
  }
}
