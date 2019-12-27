package com.marmoush.jutils.domain.port.crud;

import com.marmoush.jutils.domain.entity.Entity;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public interface EntityReadRepo<T extends Entity<?>> {
  Mono<Option<T>> get(String id);
}
