package com.marmoush.jutils.general.adapter.crud.postgresql;

import com.marmoush.jutils.general.domain.entity.Entity;
import com.marmoush.jutils.general.domain.port.crud.EntityReadRepo;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public class PgReadRepo<T extends Entity<?>> implements EntityReadRepo<T> {

  @Override
  public Mono<Option<T>> get(String id) {
    return null;
  }
}
