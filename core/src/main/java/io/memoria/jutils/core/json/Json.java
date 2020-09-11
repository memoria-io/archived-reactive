package io.memoria.jutils.core.json;

import io.vavr.control.Try;

public interface Json {
  <T> Try<T> deserialize(String str);

  <T> Try<T> deserialize(String str, Class<T> tClass);

  <T> String serialize(T t);
}
