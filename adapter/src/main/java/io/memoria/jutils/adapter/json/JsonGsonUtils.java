package io.memoria.jutils.adapter.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.vavr.CheckedFunction1;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;
import io.vavr.control.Try;

import java.io.IOException;

import static io.memoria.jutils.adapter.json.JsonException.undefinedProperty;

public class JsonGsonUtils {
  public static <T> Try<T> deserialize(JsonReader in, Map<String, TypeAdapter<? extends T>> mappers) {
    return Try.of(() -> {
      in.beginObject();
      T obj = null;
      while (in.hasNext()) {
        obj = mappers.get(in.nextName()).getOrElseThrow(JsonException::noAdapterFound).read(in);
      }
      in.endObject();
      if (obj == null)
        throw undefinedProperty();
      return obj;
    });
  }

  public static <T> Try<List<T>> deserializeArray(JsonReader in, TypeAdapter<T> typeAdapter) {
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

  public static <T> Try<List<T>> deserializeArray(JsonReader in, CheckedFunction1<JsonReader, T> reader) {
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

  public static <T extends Number> void serializeArray(JsonWriter out, Traversable<T> traversable) throws IOException {
    out.beginArray();
    for (T t : traversable) {
      out.value(t.toString());
    }
    out.endArray();
  }

  public static <E extends T, T> void serializeArray(JsonWriter out,
                                                     TypeAdapter<T> typeAdapter,
                                                     Traversable<E> traversable) throws IOException {
    out.beginArray();
    for (T t : traversable) {
      typeAdapter.write(out, t);
    }
    out.endArray();
  }

  public static void serializeStringArray(JsonWriter out, Traversable<String> traversable) throws IOException {
    out.beginArray();
    for (String t : traversable) {
      out.value(t);
    }
    out.endArray();
  }

  private JsonGsonUtils() {}
}
