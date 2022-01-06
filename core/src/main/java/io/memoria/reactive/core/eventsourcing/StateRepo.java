package io.memoria.reactive.core.eventsourcing;

import reactor.core.publisher.Mono;

public interface StateRepo {
  Mono<State> get(String key);

  Mono<Void> put(String key, State state);
}
