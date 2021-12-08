package io.memoria.reactive.core.eventsourcing;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStore {
  Mono<Integer> index();

  Mono<Integer> publish(int idx, Command command);

  Flux<Command> subscribe(int offset);
}
