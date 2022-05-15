package io.memoria.reactive.core.text;

import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import io.vavr.Function1;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

public interface TextTransformer {
  default <T> Function1<String, Try<T>> blockingDeserialize(Class<T> tClass) {
    return str -> blockingDeserialize(str, tClass);
  }

  <T> Try<T> blockingDeserialize(String str, Class<T> tClass);

  default <T> Function1<T, Try<String>> blockingSerialize() {
    return this::blockingSerialize;
  }

  <T> Try<String> blockingSerialize(T t);

  default <T> Function1<String, Mono<T>> deserialize(Class<T> tClass) {
    return str -> deserialize(str, tClass);
  }

  default <T> Mono<T> deserialize(String str, Class<T> tClass) {
    return Mono.fromCallable(() -> blockingDeserialize(str, tClass)).flatMap(ReactorVavrUtils::toMono);
  }

  default <T> Function1<T, Mono<String>> serialize() {
    return this::serialize;
  }

  default <T> Mono<String> serialize(T t) {
    return Mono.fromCallable(() -> blockingSerialize(t)).flatMap(ReactorVavrUtils::toMono);
  }
}
