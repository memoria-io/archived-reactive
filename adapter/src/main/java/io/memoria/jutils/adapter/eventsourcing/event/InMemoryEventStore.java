package io.memoria.jutils.adapter.eventsourcing.event;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public record InMemoryEventStore<E extends Event>(Map<String, List<E>> db) implements EventStore<E> {
  @Override
  public Mono<Void> add(String streamId, E e) {
    return Mono.fromRunnable(() -> {
      if (!db.containsKey(streamId)) {
        db.put(streamId, new LinkedList<>());
      }
      db.get(streamId).add(e);
    });
  }

  @Override
  public Mono<Void> add(String streamId, Iterable<E> iterable) {
    return Mono.fromRunnable(() -> {
      if (!db.containsKey(streamId)) {
        db.put(streamId, new LinkedList<>());
      }
      iterable.forEach(e -> db.get(streamId).add(e));
    });
  }

  @Override
  public Mono<Boolean> exists(String streamId) {
    return Mono.fromCallable(() -> db.containsKey(streamId));
  }

  @Override
  public Flux<E> stream(String streamId) {
    return Mono.fromCallable(() -> Option.of(db.get(streamId)))
               .map(o -> o.getOrElse(new LinkedList<>()))
               .flatMapMany(Flux::fromIterable);
  }
}
