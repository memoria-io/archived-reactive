package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;
import reactor.core.publisher.Flux;

public interface Pub<T> {
  Flux<Id> publish(Flux<T> msgs);
}
