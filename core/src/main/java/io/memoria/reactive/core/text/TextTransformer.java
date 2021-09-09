package io.memoria.reactive.core.text;

import io.vavr.Function1;
import reactor.core.publisher.Mono;

public interface TextTransformer {
  <T> Mono<T> deserialize(String str, Class<T> tClass);

  default <T> Function1<String, Mono<T>> deserialize(Class<T> tClass) {
    return t -> deserialize(t, tClass);
  }

  <T> Mono<String> serialize(T t);
}
