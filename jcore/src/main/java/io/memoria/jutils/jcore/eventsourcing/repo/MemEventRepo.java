package io.memoria.jutils.jcore.eventsourcing.repo;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ClassCanBeRecord")
public class MemEventRepo implements EventRepo {
  private final ConcurrentHashMap<String, List<Event>> db;

  public MemEventRepo(ConcurrentHashMap<String, List<Event>> db) {
    this.db = db;
  }

  @Override
  public Mono<Void> add(String aggregate, List<Event> events) {
    return Mono.fromRunnable(() -> {
      this.db.computeIfPresent(aggregate, (k, v) -> v.appendAll(events));
      this.db.computeIfAbsent(aggregate, key -> events);
    });
  }

  @Override
  public Mono<List<Event>> find(String aggregate) {
    return Mono.fromCallable(() -> db.get(aggregate));
  }
}
