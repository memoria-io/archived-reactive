package io.memoria.jutils.security;

import reactor.core.publisher.Mono;

public interface Hasher {
  String blockingHash(String password, String salt);

  Mono<String> hash(String password, String salt);
}
