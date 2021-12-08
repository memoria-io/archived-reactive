package io.memoria.reactive.core.eventsourcing;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStore {
  Mono<Integer> index();

  Mono<Event> publish(Event event);

  Flux<Event> subscribe(int offset);
}
