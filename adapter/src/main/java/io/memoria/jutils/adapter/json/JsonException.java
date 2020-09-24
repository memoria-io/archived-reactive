package io.memoria.jutils.adapter.json;

import java.io.IOException;

public class JsonException extends IOException {
  public static JsonException noMatchingAdapter(String jsonProperty) {
    return new JsonException("Json property [" + jsonProperty + "] has no matching adapter");
  }

  public static JsonException notFound(String jsonProperty) {
    return new JsonException("Json property [" + jsonProperty + "] was not found in the Json string");
  }

  public static JsonException unknown(String jsonProperty) {
    return new JsonException("Json property [" + jsonProperty + "] is unknown");
  }

  public static <T> JsonException unsupportedRead(Class<T> type) {
    return new JsonException("Deserialization is not supported for the type [" + type.getSimpleName() + "]");
  }

  public static <T> JsonException unsupportedWrite(Class<T> type) {
    return new JsonException("Serialization is not supported for the type [" + type.getSimpleName() + "]");
  }

  public JsonException(String message) {
    super(message);
  }
}
