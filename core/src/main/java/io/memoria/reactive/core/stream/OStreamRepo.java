package io.memoria.reactive.core.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Ordered Stream Repo
 */
public interface OStreamRepo {
  Mono<String> create(String topic);

  Mono<Long> publish(String topic, OMsg oMsg);

  Mono<Long> size(String topic);

  Flux<OMsg> subscribe(String topic, long skipped);
}
