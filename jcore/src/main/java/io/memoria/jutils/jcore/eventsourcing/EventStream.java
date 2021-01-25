package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStream {
  <E extends Event> Flux<E> add(Id aggId, Flux<E> Messages);

  Mono<Boolean> exists(Id aggId);

  <E extends Event> Flux<E> stream(Id aggId, Class<E> as);
}
