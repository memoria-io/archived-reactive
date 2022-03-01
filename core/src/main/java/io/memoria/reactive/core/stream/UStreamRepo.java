package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Un ordered Stream repo
 */
public interface UStreamRepo {
  default Mono<Id> publish(String topic, int partition, UMsg msg) {
    return publish(topic, partition, Flux.just(msg)).next();
  }

  Flux<Id> publish(String topic, int partition, Flux<UMsg> msgs);

  Flux<UMsg> subscribe(String topic, int partition, long skipped);
}