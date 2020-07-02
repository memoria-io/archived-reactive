package io.memoria.jutils.core.domain.port;

import io.vavr.control.Try;

import java.util.Map;

public interface Json {
  Try<Map<String, Object>> toMap(String str);

  <T> Try<T> toObject(String str, Class<T> tClass);

  <T> String toString(T t);
}
