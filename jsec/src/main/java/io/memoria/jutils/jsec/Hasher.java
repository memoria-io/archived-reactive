package io.memoria.jutils.jsec;

import reactor.core.publisher.Mono;

public interface Hasher {
  String blockingHash(String password, String salt);

  Mono<String> hash(String password, String salt);
}
