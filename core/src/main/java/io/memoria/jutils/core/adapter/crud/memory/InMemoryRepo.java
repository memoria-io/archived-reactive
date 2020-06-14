package io.memoria.jutils.core.adapter.crud.memory;

import io.memoria.jutils.core.domain.entity.Entity;
import io.memoria.jutils.core.domain.port.crud.EntityReadRepo;
import io.memoria.jutils.core.domain.port.crud.EntityRepo;
import io.memoria.jutils.core.domain.port.crud.EntityWriteRepo;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import java.util.Map;

public class InMemoryRepo<T extends Entity<?>> implements EntityRepo<T> {
  protected final Map<String, T> db;
  private final EntityReadRepo<T> entityReadRepo;
  private final EntityWriteRepo<T> entityWriteRepo;

  public InMemoryRepo(Map<String, T> db) {
    this.db = db;
    this.entityReadRepo = new InMemoryReadRepo<>(db);
    this.entityWriteRepo = new InMemoryWriteRepo<>(db);
  }

  @Override
  public Mono<Try<T>> create(T t) {
    return this.entityWriteRepo.create(t);
  }

  @Override
  public Mono<Try<T>> update(T t) {
    return this.entityWriteRepo.update(t);
  }

  @Override
  public Mono<Void> delete(String id) {
    return this.entityWriteRepo.delete(id);
  }

  @Override
  public Mono<Try<T>> get(String id) {
    return this.entityReadRepo.get(id);
  }

  @Override
  public Mono<Try<Boolean>> exists(String id) {
    return this.entityReadRepo.exists(id);
  }
}
