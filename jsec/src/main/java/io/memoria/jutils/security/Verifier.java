package io.memoria.jutils.security;

import reactor.core.publisher.Mono;

public interface Verifier {
  boolean blockingVerify(String password, String hash, String salt);

  Mono<Boolean> verify(String password, String hash, String salt);
}
