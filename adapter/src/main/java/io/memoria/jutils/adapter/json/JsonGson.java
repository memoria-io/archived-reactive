package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import io.memoria.jutils.core.json.Json;
import io.vavr.control.Try;

import java.lang.reflect.Type;

public record JsonGson(Gson gson) implements Json {
  @Override
  public <T> Try<T> deserialize(String str, Type type) {
    return Try.of(() -> gson.fromJson(str, type));
  }

  @Override
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    return Try.of(() -> gson.fromJson(str, tClass));
  }

  @Override
  public <T> String serialize(T t) {
    return gson.toJson(t);
  }
}
