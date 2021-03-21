package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStore {
  Mono<Long> publish(List<Event> msg);

  Flux<Event> subscribe(long offset);

  Mono<Event> last();

  /**
   * Used for initial state building
   *
   * @return Flux which finishes when it meets the result of {@link EventStore#last()}
   */
  default Flux<Event> subscribeToLast() {
    return last().flatMapMany(l -> subscribe(0).takeUntil(e -> e.eventId().equals(l.eventId())));
  }
}
