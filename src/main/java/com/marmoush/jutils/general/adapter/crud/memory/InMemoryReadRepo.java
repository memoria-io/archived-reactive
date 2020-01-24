package com.marmoush.jutils.general.adapter.crud.memory;

import com.marmoush.jutils.general.domain.entity.Entity;
import com.marmoush.jutils.general.domain.port.crud.EntityReadRepo;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

import java.util.Map;

public class InMemoryReadRepo<T extends Entity<?>> implements EntityReadRepo<T> {
  protected final Map<String, T> db;

  public InMemoryReadRepo(Map<String, T> db) {
    this.db = db;
  }

  @Override
  public Mono<Option<T>> get(String id) {
    return Mono.just(Option.of(db.get(id)));
  }
}
