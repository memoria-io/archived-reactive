package io.memoria.reactive.core.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Ordered Stream Repo
 */
public interface OStreamRepo {
  Mono<Long> publish(String topic, int partition, OMsg oMsg);

  Mono<Long> size(String topic, int partition);

  Flux<OMsg> subscribe(String topic, int partition, long skipped);
}
