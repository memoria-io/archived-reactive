package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public interface EventStore {
  Mono<Predicate<Event>> endPred(String topic, int partition);

  Mono<Boolean> exists(String topic);

  Mono<List<Event>> publish(String topic, int partition, List<Event> events);

  default Flux<Event> readAll(String topic, int partition) {
    return endPred(topic, partition).flatMapMany(ep -> subscribe(topic, partition, 0).takeUntil(ep));
  }

  Flux<Event> subscribe(String topic, int partition, int offset);
}
