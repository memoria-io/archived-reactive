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
  public Mono<Void> createTopic(String topic) {
    return Mono.fromRunnable(() -> this.db.put(topic, List.empty()));
  }

  @Override
  public Mono<Integer> add(String topic, List<Event> events) {
    return Mono.fromCallable(() -> {
      this.db.computeIfPresent(topic, (k, v) -> v.appendAll(events));
      if (this.db.get(topic) == null)
        throw new NullPointerException();
      return events.size();
    });
  }

  @Override
  public Mono<List<Event>> find(String topic) {
    return Mono.fromCallable(() -> db.get(topic));
  }
}
