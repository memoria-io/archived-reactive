package com.marmoush.jutils.adapter.memory;

import com.marmoush.jutils.domain.entity.Entity;
import com.marmoush.jutils.domain.port.EntityWriteRepo;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static com.marmoush.jutils.domain.error.AlreadyExists.ALREADY_EXISTS;
import static com.marmoush.jutils.domain.error.NotFound.NOT_FOUND;

public class InMemoryWriteRepo<T extends Entity<?>> implements EntityWriteRepo<T> {
  protected final Map<String, T> db = new HashMap<>();

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
