package io.memoria.jutils.adapter.transformer.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.vavr.CheckedFunction1;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;
import io.vavr.control.Try;

import java.io.IOException;

import static io.memoria.jutils.core.transformer.json.JsonException.noMatchingAdapter;

public class JsonGsonUtils {
  public static <T> Try<T> deserialize(JsonReader in, Map<String, TypeAdapter<? extends T>> mappers) {
    return Try.of(() -> {
      in.beginObject();
      T obj = null;
      if (in.hasNext()) {
        var nextName = in.nextName();
        obj = mappers.get(nextName).getOrElseThrow(() -> noMatchingAdapter(nextName)).read(in);
      }
      in.endObject();
      return obj;
    });
  }

  public static <T> Try<List<T>> deserializeAsList(JsonReader in, TypeAdapter<T> typeAdapter) {
    return Try.of(() -> {
      var list = List.<T>empty();
      in.beginArray();
      while (in.hasNext()) {
        list = list.append(typeAdapter.read(in));
      }
      in.endArray();
      return list;
    });
  }

  public static <T> Try<List<T>> deserializeAsList(JsonReader in, CheckedFunction1<JsonReader, T> reader) {
    return Try.of(() -> {
      in.beginArray();
      var list = List.<T>empty();
      while (in.hasNext()) {
        list = list.append(reader.apply(in));
      }
      in.endArray();
      return list;
    });
  }

  public static <E extends T, T> void serialize(JsonWriter out, TypeAdapter<T> typeAdapter, Traversable<E> traversable)
          throws IOException {
    out.beginArray();
    for (T t : traversable) {
      typeAdapter.write(out, t);
    }
    out.endArray();
  }

  public static void serialize(JsonWriter out, Traversable<String> traversable) throws IOException {
    out.beginArray();
    for (String t : traversable) {
      out.value(t);
    }
    out.endArray();
  }

  public static <T extends Number> void serializeNumber(JsonWriter out, Traversable<T> traversable) throws IOException {
    out.beginArray();
    for (T t : traversable) {
      out.value(t.toString());
    }
    out.endArray();
  }

  private JsonGsonUtils() {}
}
