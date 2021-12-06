package io.memoria.reactive.core.eventsourcing.mem;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateStore;
import io.memoria.reactive.core.id.Id;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

public record MemStateStore(ConcurrentHashMap<Id, State> db) implements StateStore {

  @Override
  public Mono<Option<State>> get(Id id) {
    return Mono.fromCallable(() -> Option.of(db.get(id)));
  }

  @Override
  public Mono<State> put(Id id, State state) {
    return Mono.fromRunnable(() -> db.put(id, state)).thenReturn(state);
  }
}
