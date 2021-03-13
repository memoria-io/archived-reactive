package io.memoria.jutils.jcore.eventsourcing;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public interface EventSubscriber {
  Mono<Boolean> exists(String topic);

  Mono<Predicate<Event>> lastEventPredicate(String topic, int partition);

  Flux<Event> subscribe(String topic, int partition, long startOffset);

  default Flux<Event> readUntilEnd(String topic, int partition) {
    return lastEventPredicate(topic, partition).flatMapMany(ep -> subscribe(topic, partition, 0).takeUntil(ep));
  }
}
