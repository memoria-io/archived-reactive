package io.memoria.jutils.core.adapter.crud.memory;

import io.memoria.jutils.core.domain.entity.Entity;
import io.memoria.jutils.core.domain.port.crud.EntityReadRepo;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import java.util.Map;

import static io.memoria.jutils.core.domain.error.NotFound.NOT_FOUND;

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
  public Mono<Try<Boolean>> exists(String id) {
    return Mono.just((db.containsKey(id)) ? Try.success(true) : Try.success(true));
  }
}
