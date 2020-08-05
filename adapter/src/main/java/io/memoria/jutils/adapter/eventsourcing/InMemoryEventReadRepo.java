package io.memoria.jutils.adapter.eventsourcing;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventReadRepo;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public record InMemoryEventReadRepo<K, E extends Event>(Map<K, Queue<E>> db) implements EventReadRepo<K, E> {

  @Override
  public Mono<Boolean> exists(K k) {
    return Mono.fromCallable(() -> db.containsKey(k));
  }

  @Override
  public Flux<E> stream(K k) {
    return Mono.fromCallable(() -> Option.of(db.get(k)))
               .map(o -> o.getOrElse(new LinkedList<>()))
               .flatMapMany(Flux::fromIterable);
  }
}
