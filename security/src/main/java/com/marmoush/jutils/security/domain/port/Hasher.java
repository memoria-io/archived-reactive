package com.marmoush.jutils.security.domain.port;

import reactor.core.publisher.Mono;

public interface Hasher {
  Mono<String> hash(String password, String salt);

  String blockingHash(String password, String salt);

  Mono<Boolean> verify(String password, String hash, String salt);

  boolean blockingVerify(String password, String hash, String salt);
}
