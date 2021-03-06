package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventSubscriber {
  Mono<Boolean> exists(Id aggId);

  <E extends Event> Flux<E> subscribe(Id aggId, long offset, Class<E> as);
}
