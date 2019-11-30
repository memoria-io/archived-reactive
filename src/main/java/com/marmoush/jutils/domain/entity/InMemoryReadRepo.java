package com.marmoush.jutils.domain.entity;

import io.vavr.control.Option;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class InMemoryReadRepo<T extends Entity<?>> implements EntityReadRepo<T> {
  protected final Map<String, T> db = new HashMap<>();

  @Override
  public Mono<Option<T>> get(String id) {
    return Mono.just(Option.of(db.get(id)));
  }

}
