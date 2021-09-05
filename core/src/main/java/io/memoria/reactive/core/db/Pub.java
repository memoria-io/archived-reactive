package io.memoria.reactive.core.db;

import reactor.core.publisher.Flux;

public interface Pub<T extends Msg> {
  Flux<Long> publish(Flux<T> msgs);
}
