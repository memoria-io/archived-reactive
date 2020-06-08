package io.memoria.jutils.core.adapter.crud.memory;

import io.memoria.jutils.core.domain.entity.Entity;
import io.memoria.jutils.core.domain.port.crud.EntityWriteRepo;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import java.util.Map;

import static io.memoria.jutils.core.domain.error.AlreadyExists.ALREADY_EXISTS;
import static io.memoria.jutils.core.domain.error.NotFound.NOT_FOUND;

public class InMemoryWriteRepo<T extends Entity<?>> implements EntityWriteRepo<T> {
  protected final Map<String, T> db;

  public InMemoryWriteRepo(Map<String, T> db) {
    this.db = db;
  }

  @Override
  public Mono<Try<T>> create(T t) {
    if (db.containsKey(t.id)) {
      return Mono.just(Try.failure(ALREADY_EXISTS));
    }
    db.put(t.id, t);
    return Mono.just(Try.success(t));
  }

  @Override
  public Mono<Try<T>> update(T t) {
    if (db.containsKey(t.id)) {
      return Mono.just(Try.success(db.put(t.id, t)));
    } else {
      return Mono.just(Try.failure(NOT_FOUND));
    }
  }

  @Override
  public Mono<Void> delete(String id) {
    db.remove(id);
    return Mono.empty();
  }
}
