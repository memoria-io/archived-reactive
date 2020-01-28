package com.marmoush.jutils.general.adapter.crud.postgresql;

import com.marmoush.jutils.general.domain.entity.Entity;
import com.marmoush.jutils.general.domain.port.crud.EntityWriteRepo;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

public class PgWriteRepo<T extends Entity<?>> implements EntityWriteRepo<T> {

  @Override
  public Mono<Try<T>> create(T t) {
    return null;
  }

  @Override
  public Mono<Try<T>> update(T t) {
    return null;
  }

  @Override
  public Mono<Void> delete(String id) {
    return null;
  }
}
