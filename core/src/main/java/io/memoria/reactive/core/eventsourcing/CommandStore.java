package io.memoria.reactive.core.eventsourcing;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStore {
  Mono<Long> last();

  Mono<Command> publish(Command command);

  Flux<Command> subscribe(long offset);
}
