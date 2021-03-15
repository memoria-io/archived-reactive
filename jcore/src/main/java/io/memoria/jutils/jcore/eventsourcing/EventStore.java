package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStore {
  Mono<Event> lastEvent(String topic, int partition);

  Mono<Boolean> exists(String topic);

  Mono<List<Event>> publish(String topic, int partition, List<Event> events);

  Flux<Event> subscribe(String topic, int partition, int offset);
}
