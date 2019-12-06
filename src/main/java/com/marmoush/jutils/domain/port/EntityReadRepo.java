package com.marmoush.jutils.domain.port;

import com.marmoush.jutils.domain.entity.Entity;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public interface EntityReadRepo<T extends Entity<?>> {
  Mono<Option<T>> get(String id);
}
