package io.memoria.jutils.core.adapter.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.memoria.jutils.core.domain.port.Json;
import io.vavr.control.Try;

import java.util.Map;
import java.util.Objects;

public class JsonGson implements Json {
  private final Gson gson;

  public JsonGson(Gson gson) {
    this.gson = gson;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    JsonGson jsonGson = (JsonGson) o;
    return gson.equals(jsonGson.gson);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gson);
  }

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
