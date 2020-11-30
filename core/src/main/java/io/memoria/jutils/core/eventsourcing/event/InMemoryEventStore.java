package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.value.Id;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryEventStore implements EventStore {
  private final Map<Id, Lock> locks;
  private final Map<Id, List<Event>> db;

  public InMemoryEventStore(Map<Id, List<Event>> db) {
    locks = new HashMap<>();
    this.db = db;
  }

  @Override
  public Mono<Event> add(Id id, Event e) {
    return Mono.fromRunnable(() -> {
      if (!db.containsKey(id)) {
        db.put(id, new ArrayList<>());
      }
      db.get(id).add(e);
    }).thenReturn(e);
  }

  public void endTransaction(Id id) {
    locks.putIfAbsent(id, new ReentrantLock());
    locks.get(id).unlock();
  }

  @Override
  public Mono<List<Event>> get(Id id) {
    return Mono.fromCallable(() -> Option.of(db.get(id))).map(o -> o.getOrElse(new ArrayList<>()));
  }

  public void startTransaction(Id id) {
    locks.putIfAbsent(id, new ReentrantLock());
    locks.get(id).lock();
  }
}
