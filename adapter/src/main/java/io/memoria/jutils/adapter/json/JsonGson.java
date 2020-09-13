package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import io.memoria.jutils.core.json.Json;
import io.vavr.collection.List;
import io.vavr.control.Try;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public record JsonGson(Gson gson) implements Json {
  public static <T> List<T> deserializeArray(JsonReader in, TypeAdapter<T> typeAdapter) throws IOException {
    var typeList = new ArrayList<T>();
    in.beginArray();
    while (in.hasNext()) {
      typeList.add(typeAdapter.read(in));
    }
    in.endArray();
    return List.ofAll(typeList);
  }

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
      var paramType = (ParameterizedType) t.getClass().getGenericSuperclass();
      var tClass = paramType.getActualTypeArguments()[0];
      gsonBuilder.registerTypeHierarchyAdapter((Class<?>) tClass, t);
    }
    return gsonBuilder.create();
  }
}
