package com.marmoush.jutils.domain.entity;

import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static com.marmoush.jutils.error.AlreadyExists.ALREADY_EXISTS;

public class InMemoryRepo<T extends Entity<?>> implements EntityRepo<T> {
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
  public Mono<Option<T>> get(String id) {
    return Mono.just(Option.of(db.get(id)));
  }

  @Override
  public Mono<Void> delete(String id) {
    db.remove(id);
    return Mono.empty();
  }
}
