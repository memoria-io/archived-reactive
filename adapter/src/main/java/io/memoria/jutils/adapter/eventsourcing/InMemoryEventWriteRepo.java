package io.memoria.jutils.adapter.eventsourcing;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventWriteRepo;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public record InMemoryEventWriteRepo<E extends Event>(Map<String, List<E>> db) implements EventWriteRepo<E> {
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
