package io.memoria.reactive.core.eventsourcing.mem;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public record MemEventStore(List<Event> db) implements EventStore {

  @Override
  public Mono<Event> publish(Event event) {
    return Mono.fromRunnable(() -> db.add(event)).thenReturn(event);
  }

  @Override
  public Flux<Event> subscribe(long offset) {
    return Flux.fromIterable(db).skip(offset);
  }

  @Override
  public Mono<Long> last() {
    return Mono.fromCallable(() -> (long) db.size());
  }
}
