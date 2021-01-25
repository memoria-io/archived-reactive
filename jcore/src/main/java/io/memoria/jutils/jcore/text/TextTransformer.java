package io.memoria.jutils.jcore.text;

import io.vavr.control.Try;

public interface TextTransformer {
  <T> Try<T> deserialize(String str, Class<T> tClass);

  <T> Try<String> serialize(T t);
}
