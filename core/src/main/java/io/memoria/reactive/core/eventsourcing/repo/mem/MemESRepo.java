package io.memoria.reactive.core.eventsourcing.repo.mem;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.repo.EventRepo;
import io.memoria.reactive.core.id.Id;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public record MemESRepo(java.util.List<Event> db) implements EventRepo {

  @Override
  public Mono<List<Event>> add(List<Event> events) {
    return Mono.fromCallable(() -> {
      this.db.addAll(events.toJavaList());
      return events;
    });
  }

  @Override
  public Mono<List<Event>> find() {
    return Mono.fromCallable(() -> List.ofAll(db));
  }

  @Override
  public Mono<List<Event>> find(Id aggId) {
    return Mono.fromCallable(() -> List.ofAll(db).filter(e -> e.aggId().equals(aggId)));
  }
}
