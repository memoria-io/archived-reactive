package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Un ordered Stream repo
 */
public interface UStreamRepo {
  Mono<Void> create(String topic);

  Mono<Id> publish(String topic, UMsg msg);

  Flux<UMsg> subscribe(String topic, int skipped);
}