package io.memoria.jutils.security.domain.port;

import reactor.core.publisher.Mono;

public interface Verifier {
  Mono<Boolean> verify(String password, String hash, String salt);

  boolean blockingVerify(String password, String hash, String salt);
}
