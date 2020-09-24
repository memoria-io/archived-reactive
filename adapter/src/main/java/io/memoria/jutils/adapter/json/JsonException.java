package io.memoria.jutils.adapter.json;

import java.io.IOException;

public class JsonException extends IOException {
  public static JsonException noAdapterFound() {
    return new JsonException("No matching type adapter found");
  }

  public static JsonException propertyNotFound(String propertyName) {
    return new JsonException("Property [" + propertyName + "] was not found");
  }

  public static JsonException undefinedProperty() {
    return new JsonException("No property found");
  }

  public static JsonException unknownProperty(String fieldName) {
    return new JsonException("The field [" + fieldName + "] is unknown");
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
