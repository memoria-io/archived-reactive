package io.memoria.jutils.core.json;

import io.vavr.control.Try;

import java.util.Map;

public interface Json {
  <T> Try<T> fromJson(String str, Class<T> tClass);

  Try<Map<String, Object>> fromJsonToMap(String str);

  <T> String toJson(T t);
}
