package io.memoria.reactive.core.db;

import reactor.core.publisher.Flux;

public interface Pub<T extends Msg> {
  Flux<T> publish(Flux<T> msgs);
}
