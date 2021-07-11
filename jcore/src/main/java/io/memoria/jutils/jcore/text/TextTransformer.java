package io.memoria.jutils.jcore.text;

import io.vavr.Function1;
import io.vavr.control.Try;

public interface TextTransformer {
  <T> Try<T> deserialize(String str, Class<T> tClass);

  <T> Try<String> serialize(T t);

  default <T> Function1<String, Try<T>> deserialize(Class<T> tClass) {
    return t -> deserialize(t, tClass);
  }
}
