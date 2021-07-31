package io.memoria.reactive.sec;

import reactor.core.publisher.Mono;

public interface Hasher {
  String blockingHash(String password, String salt);

  Mono<String> hash(String password, String salt);
}
