package com.marmoush.jutils.core.adapter.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.marmoush.jutils.core.domain.port.Json;
import io.vavr.control.Try;

import java.util.Map;

public class JsonGson implements Json {
  private final Gson gson;

  public JsonGson(Gson gson) {
    this.gson = gson;
  }

  @Override
  public <T> Try<T> toObject(String str, Class<T> tClass) {
    return Try.of(() -> gson.fromJson(str, tClass));
  }

  @Override
  public Try<Map<String, Object>> toMap(String str) {
    return Try.of(() -> gson.fromJson(str, new TypeToken<Map<String, Object>>() {}.getType()));
  }

  @Override
  public <T> String toJsonString(T t) {
    return gson.toJson(t);
  }
}
