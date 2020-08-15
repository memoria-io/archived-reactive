package io.memoria.jutils.adapter.eventsourcing;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventReadRepo;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public record InMemoryEventReadRepo<E extends Event>(Map<String, Queue<E>> db) implements EventReadRepo<E> {

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
