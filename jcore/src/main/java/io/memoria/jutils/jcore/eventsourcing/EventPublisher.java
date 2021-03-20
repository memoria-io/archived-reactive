package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.id.Id;
import reactor.core.publisher.Flux;

public interface EventPublisher {
  <E extends Event> Flux<E> publish(Id aggId, Flux<E> events);
}
