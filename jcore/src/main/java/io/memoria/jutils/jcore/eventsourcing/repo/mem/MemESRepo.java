package io.memoria.jutils.jcore.eventsourcing.repo.mem;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.repo.EventRepo;
import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public record MemESRepo(java.util.List<Event> db) implements EventRepo {

  @Override
  public Mono<Integer> add(List<Event> events) {
    return Mono.fromCallable(() -> {
      this.db.addAll(events.toJavaList());
      return events.size();
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
