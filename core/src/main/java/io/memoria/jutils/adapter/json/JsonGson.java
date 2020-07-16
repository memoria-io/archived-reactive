package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.memoria.jutils.core.json.Json;
import io.vavr.control.Try;

import java.util.Map;

public record JsonGson(Gson gson) implements Json {

  @Override
  public Try<Map<String, Object>> toMap(String str) {
    return Try.of(() -> gson.fromJson(str, new TypeToken<Map<String, Object>>() {}.getType()));
  }

  @Override
  public <T> Try<T> toObject(String str, Class<T> tClass) {
    return Try.of(() -> gson.fromJson(str, tClass));
  }

  @Override
  public <T> String toString(T t) {
    return gson.toJson(t);
  }
}
