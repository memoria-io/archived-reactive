package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.memoria.jutils.core.json.Json;
import io.vavr.control.Try;

public record JsonGson(Gson gson) implements Json {

  @Override
  public <T> Try<T> deserialize(String str) {
    return Try.of(() -> gson.fromJson(str, new TypeToken<T>() {}.getType()));
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
