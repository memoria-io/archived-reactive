package com.marmoush.jutils.core.adapter.crud.memory;

import com.marmoush.jutils.core.domain.entity.Entity;
import com.marmoush.jutils.core.domain.port.crud.EntityReadRepo;
import io.vavr.control.*;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.marmoush.jutils.core.domain.error.NotFound.NOT_FOUND;

public class InMemoryReadRepo<T extends Entity<?>> implements EntityReadRepo<T> {
  protected final Map<String, T> db;

  public InMemoryReadRepo(Map<String, T> db) {
    this.db = db;
  }

  @Override
  public Mono<Try<T>> get(String id) {
    return Mono.just(Option.of(db.get(id)).toTry(() -> NOT_FOUND));
  }

  @Override
  public Mono<Try<Void>> exists(String id) {
    return Mono.just((db.containsValue(id)) ? Try.success(null) : Try.failure(NOT_FOUND));
  }
}
