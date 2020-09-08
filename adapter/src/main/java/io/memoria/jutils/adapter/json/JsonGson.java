package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.memoria.jutils.core.json.Json;
import io.vavr.control.Try;

import java.util.Map;

public record JsonGson(Gson gson) implements Json {

  @Override
  public <T> Try<T> fromJson(String str, Class<T> tClass) {
    return Try.of(() -> gson.fromJson(str, tClass));
  }

  @Override
  public Try<Map<String, Object>> fromJsonToMap(String str) {
    return Try.of(() -> gson.fromJson(str, new TypeToken<Map<String, Object>>() {}.getType()));
  }

  @Override
  public <T> String toJson(T t) {
    return gson.toJson(t);
  }
}
