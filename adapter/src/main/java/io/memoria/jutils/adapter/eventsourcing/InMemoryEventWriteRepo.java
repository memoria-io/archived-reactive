package io.memoria.jutils.adapter.eventsourcing;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventWriteRepo;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public record InMemoryEventWriteRepo<E extends Event>(Map<String, Queue<E>> db) implements EventWriteRepo<E> {
  @Override
  public Mono<Void> add(String streamId, E e) {
    return Mono.fromRunnable(() -> {
      if (!db.containsKey(streamId)) {
        db.put(streamId, new LinkedList<>());
      }
      db.get(streamId).add(e);
    });
  }
}
