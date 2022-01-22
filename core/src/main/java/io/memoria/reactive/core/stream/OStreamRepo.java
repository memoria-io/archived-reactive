package io.memoria.reactive.core.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Ordered Stream Repo
 */
public interface OStreamRepo {
  Mono<Void> create(String topic);

  Mono<Integer> publish(String topic, OMsg oMsg);

  Mono<Integer> size(String topic);

  Flux<OMsg> subscribe(String topic, int skipped);
}
