package io.memoria.jutils.core.eventsourcing.state;

import io.memoria.jutils.core.value.Id;
import io.vavr.control.Option;

import java.util.concurrent.ConcurrentMap;

public class InMemoryStateStore<S extends State> implements BlockingStateStore<S> {

  private final ConcurrentMap<Id, S> db;

  public InMemoryStateStore(ConcurrentMap<Id, S> db) {
    this.db = db;
  }

  @Override
  public Option<S> get(Id id) {
    return Option.of(this.db.get(id));
  }

  @Override
  public S save(S s) {
    this.db.put(s.id(), s);
    return s;
  }
}