package io.memoria.reactive.core.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Un ordered Stream repo
 */
public interface UStreamRepo extends StreamRepo {
  Mono<UMsg> publish(String topic, UMsg msg);

  Flux<UMsg> subscribe(String topic, int skipped);
}