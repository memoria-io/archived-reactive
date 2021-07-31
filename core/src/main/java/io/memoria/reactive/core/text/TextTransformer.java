package io.memoria.reactive.core.text;

import io.vavr.Function1;
import io.vavr.control.Try;

public interface TextTransformer {
  <T> Try<T> deserialize(String str, Class<T> tClass);

  default <T> Function1<String, Try<T>> deserialize(Class<T> tClass) {
    return t -> deserialize(t, tClass);
  }

  <T> Try<String> serialize(T t);
}
