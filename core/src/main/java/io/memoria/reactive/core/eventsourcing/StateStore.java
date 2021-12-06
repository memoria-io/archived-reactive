package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public interface StateStore {
  Mono<Option<State>> get(Id id);

  default Mono<State> get(Id id, State defaultState) {
    return get(id).map(opt -> opt.getOrElse(defaultState));
  }

  Mono<State> put(Id id, State state);
}
