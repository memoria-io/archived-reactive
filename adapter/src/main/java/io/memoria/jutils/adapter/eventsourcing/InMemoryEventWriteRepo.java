package io.memoria.jutils.adapter.eventsourcing;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventWriteRepo;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public record InMemoryEventWriteRepo<K, E extends Event<?>>(Map<K, Queue<E>> db) implements EventWriteRepo<K, E> {
  @Override
  public Mono<Void> add(K k, E e) {
    return Mono.fromRunnable(() -> {
      if (!db.containsKey(k)) {
        db.put(k, new LinkedList<>());
      }
      db.get(k).add(e);
    });
  }
}
