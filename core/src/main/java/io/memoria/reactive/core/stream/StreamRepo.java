package io.memoria.reactive.core.stream;

import reactor.core.publisher.Mono;

public interface StreamRepo {
  Mono<Void> create(String topic);

  Mono<Integer> size(String topic);
}
