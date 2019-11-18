package com.marmoush.jutils.json;

import io.vavr.control.Try;

import java.util.Map;

public interface Json {
  <T> Try<T> toObject(String str, Class<T> tClass);

  Try<Map<String, Object>> toMap(String str);

  <T> String toJsonString(T tclass);
}
