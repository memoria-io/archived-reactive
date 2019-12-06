package com.marmoush.jutils.adapter.memory;

import com.marmoush.jutils.domain.entity.Entity;
import com.marmoush.jutils.domain.port.EntityReadRepo;
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
