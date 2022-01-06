package io.memoria.reactive.core.eventsourcing.mem;

import io.memoria.reactive.core.eventsourcing.State;
import io.memoria.reactive.core.eventsourcing.StateRepo;
import reactor.core.publisher.Mono;

import java.util.Map;

public record MemStateRepo(Map<String, State> db) implements StateRepo {

  @Override
  public Mono<State> get(String key) {
    return Mono.fromCallable(() -> db.get(key));
  }

  @Override
  public Mono<Void> put(String key, State state) {
    return Mono.fromRunnable(() -> db.put(key, state));
  }
}
