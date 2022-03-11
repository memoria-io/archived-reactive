package io.memoria.reactive.core.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Stream {
  Flux<Msg> publish(Flux<Msg> msgs);

  Mono<Long> size(String topic, int partition);

  Flux<Msg> subscribe(String topic, int partition, long skipped);
}
