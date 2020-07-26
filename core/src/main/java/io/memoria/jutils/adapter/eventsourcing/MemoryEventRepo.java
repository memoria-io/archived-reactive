package io.memoria.jutils.adapter.eventsourcing;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public record MemoryEventRepo<K, E extends Event>(Map<K, Queue<E>>db) implements EventRepo<K, E> {

  @Override
  public Mono<Void> add(K k, E e) {
    return Mono.fromRunnable(() -> {
      if (!db.containsKey(k)) {
        db.put(k, new LinkedList<>());
      }
      db.get(k).add(e);
    });
  }

  @Override
  public Mono<Boolean> exists(K k) {
    return Mono.fromCallable(() -> db.containsKey(k));
  }

  @Override
  public Flux<E> stream(K k) {
    return Mono.fromCallable(() -> db.get(k)).flatMapMany(Flux::fromIterable);
  }
}
