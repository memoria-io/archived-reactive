package io.memoria.reactive.core.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Ordered Stream Repo
 */
public interface OStreamRepo extends StreamRepo {
  Mono<OMsg> publish(String topic, OMsg oMsg);

  Flux<OMsg> subscribe(String topic, int skipped);
}
