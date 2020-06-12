package io.memoria.jutils.security.domain.port;

import reactor.core.publisher.Mono;

public interface Hasher {
  Mono<String> hash(String password, String salt);

  String blockingHash(String password, String salt);
}
