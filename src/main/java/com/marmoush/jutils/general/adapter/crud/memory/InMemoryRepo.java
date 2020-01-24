package com.marmoush.jutils.general.adapter.crud.memory;

import com.marmoush.jutils.general.domain.entity.Entity;
import com.marmoush.jutils.general.domain.port.crud.EntityReadRepo;
import com.marmoush.jutils.general.domain.port.crud.EntityRepo;
import com.marmoush.jutils.general.domain.port.crud.EntityWriteRepo;
import io.vavr.control.Option;
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
  public Mono<Option<T>> get(String id) {
    return this.entityReadRepo.get(id);
  }

  @Override
  public Mono<Boolean> exists(String id) {
    return this.entityReadRepo.exists(id);
  }

  @Override
  public Mono<Void> delete(String id) {
    return this.entityWriteRepo.delete(id);
  }
}
