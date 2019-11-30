package com.marmoush.jutils.domain.entity;

import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public interface EntityReadRepo<T extends Entity<?>> {
  Mono<Option<T>> get(String id);
}
