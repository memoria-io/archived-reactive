package com.marmoush.jutils.domain.port.eventsourcing;

import reactor.core.publisher.Mono;

public interface Evolver<T> {
  Mono<T> evolve(T current, Event event);
}
