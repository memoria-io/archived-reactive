package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import io.memoria.jutils.core.json.Json;
import io.vavr.control.Try;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public record JsonGson(Gson gson) implements Json {

  public JsonGson(TypeAdapter<?>... typeAdapters) {
    this(new GsonBuilder(), typeAdapters);
  }

  public JsonGson(GsonBuilder gsonBuilder, TypeAdapter<?>... typeAdapters) {
    this(create(gsonBuilder, typeAdapters));
  }

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

  private static Gson create(GsonBuilder gsonBuilder, TypeAdapter<?>... typeAdapters) {
    for (TypeAdapter<?> t : typeAdapters) {
      var tClass = ((ParameterizedType) t.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
      gsonBuilder.registerTypeAdapter(tClass, t);
    }
    return gsonBuilder.create();
  }
}
