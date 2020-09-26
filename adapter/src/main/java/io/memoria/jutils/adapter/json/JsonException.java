package io.memoria.jutils.adapter.json;

import java.io.IOException;

public class JsonException extends IOException {

  public static JsonException noMatchingAdapter(String jsonProperty) {
    return new JsonException("Json property [%s] has no matching adapter".formatted(jsonProperty));
  }

  public static JsonException notFound(String jsonProperty) {
    return new JsonException("Json property [%s] was not found in the Json string".formatted(jsonProperty));
  }

  public static JsonException unknown(String jsonProperty) {
    return new JsonException("Json property [%s] is unknown".formatted(jsonProperty));
  }

  public static <T> JsonException unsupportedRead(Class<T> type) {
    return new JsonException("Deserialization is not supported for the type [%s]".formatted(type.getSimpleName()));
  }

  public static <T> JsonException unsupportedWrite(Class<T> type) {
    return new JsonException("Serialization is not supported for the type [%s]".formatted(type.getSimpleName()));
  }

  public JsonException(String message) {
    super(message);
  }
}
