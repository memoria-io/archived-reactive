package io.memoria.reactive.core.eventsourcing.mem;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public record MemEventStore(List<Event> db) implements EventStore {

  @Override
  public Mono<Integer> index() {
    return Mono.fromCallable(db::size);
  }

  @Override
  public Mono<Integer> publish(int idx, Event event) {
    return Mono.fromRunnable(() -> {
      if (db.size() == idx)
        db.add(event);
      else
        throw new IndexOutOfBoundsException(db.size());
    }).thenReturn(db.size());
  }

  @Override
  public Flux<Event> subscribe(int offset) {
    return Flux.fromIterable(db).skip(offset);
  }
}
