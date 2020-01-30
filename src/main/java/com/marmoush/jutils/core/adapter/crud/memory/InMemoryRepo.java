package com.marmoush.jutils.core.adapter.crud.memory;

import com.marmoush.jutils.core.domain.entity.Entity;
import com.marmoush.jutils.core.domain.port.crud.*;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import java.util.Map;

public class InMemoryRepo<T extends Entity<?>> implements EntityRepo<T> {
  protected final Map<String, T> db;
  private EntityReadRepo<T> entityReadRepo;
  private EntityWriteRepo<T> entityWriteRepo;

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
  public Mono<Try<Void>> exists(String id) {
    return this.entityReadRepo.exists(id);
  }
}
