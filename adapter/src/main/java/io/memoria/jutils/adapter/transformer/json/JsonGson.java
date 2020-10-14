package io.memoria.jutils.adapter.transformer.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import io.memoria.jutils.core.dto.DTO;
import io.memoria.jutils.core.transformer.json.Json;
import io.vavr.control.Try;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static java.util.function.Function.identity;

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
  public <T> Try<T> deserializeByDTO(String str, Class<? extends DTO<T>> tClass) {
    return Try.of(() -> gson.fromJson(str, tClass).get()).flatMap(identity());
  }

  @Override
  public <T> Try<String> serialize(T t) {
    return Try.success(gson.toJson(t));
  }

  private static Gson create(GsonBuilder gsonBuilder, TypeAdapter<?>... typeAdapters) {
    for (TypeAdapter<?> t : typeAdapters) {
      var paramType = (ParameterizedType) t.getClass().getGenericSuperclass();
      var tClass = paramType.getActualTypeArguments()[0];
      gsonBuilder.registerTypeHierarchyAdapter((Class<?>) tClass, t);
    }
    return gsonBuilder.create();
  }
}
