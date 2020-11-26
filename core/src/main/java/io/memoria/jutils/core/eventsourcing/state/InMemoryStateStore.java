package io.memoria.jutils.core.eventsourcing.state;

import io.memoria.jutils.core.value.Id;
import io.vavr.control.Option;

import java.util.Map;

public class InMemoryStateStore<S extends State> implements StateStore<S> {

  private final Map<Id, S> db;

  public InMemoryStateStore(Map<Id, S> db) {
    this.db = db;
  }

  @Override
  public synchronized S save(S state) {
    return this.db.put(state.id(), state);
  }

  @Override
  public Option<S> get(Id id) {
    return Option.of(this.db.get(id));
  }
}
