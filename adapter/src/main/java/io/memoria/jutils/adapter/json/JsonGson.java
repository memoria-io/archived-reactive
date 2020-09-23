package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.memoria.jutils.core.json.Json;
import io.vavr.CheckedFunction1;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import io.vavr.control.Try;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public record JsonGson(Gson gson) implements Json {
  public static <T> Try<List<T>> deserializeArray(JsonReader in, CheckedFunction1<JsonReader, T> reader)
          throws IOException {
    in.beginArray();
    var tryList = Try.of(() -> {
      var list = List.<T>empty();
      while (in.hasNext()) {
        list = list.append(reader.apply(in));
      }
      return list;
    });
    in.endArray();
    return tryList;
  }

  public static <T> List<T> deserializeArray(JsonReader in, TypeAdapter<T> typeAdapter) throws IOException {
    var list = List.<T>empty();
    in.beginArray();
    while (in.hasNext()) {
      list = list.append(typeAdapter.read(in));
    }
    in.endArray();
    return list;
  }

  public static <T> List<T> serializeArray(JsonWriter out, Traversable<T> traversable) throws IOException {
    var list = List.<T>empty();
    out.beginArray();
    for (T t : traversable) {
      out.value(t.toString());
    }
    out.endArray();
    return List.ofAll(list);
  }

  public static <T> List<T> serializeArray(JsonWriter out, TypeAdapter<T> typeAdapter, Traversable<T> traversable)
          throws IOException {
    var list = List.<T>empty();
    out.beginArray();
    for (T t : traversable) {
      typeAdapter.write(out, t);
    }
    out.endArray();
    return List.ofAll(list);
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
