package com.marmoush.jutils.security.domain.port;

import reactor.core.publisher.Mono;

public interface Hasher {
  Mono<String> hash(String password, String salt);

  Mono<Boolean> verify(String password, String hash, String salt);
}
