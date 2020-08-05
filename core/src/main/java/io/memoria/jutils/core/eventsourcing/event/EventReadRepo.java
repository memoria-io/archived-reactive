package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.state.State;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventReadRepo<K, E extends Event<? extends State>> {
  Mono<Boolean> exists(K k);

  Flux<E> stream(K k);
}
