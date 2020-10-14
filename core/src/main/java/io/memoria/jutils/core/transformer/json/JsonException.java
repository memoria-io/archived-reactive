package io.memoria.jutils.core.transformer.json;

import io.vavr.collection.List;

import java.io.IOException;

public class JsonException extends IOException {
  public static JsonException noMatchingAdapter(String jsonProperty) {
    return new JsonException("Json property [%s] has no matching adapter".formatted(jsonProperty));
  }

  public static JsonException notFound(String jsonProperty) {
    return new JsonException("Json property [%s] was not found in the Json string".formatted(jsonProperty));
  }

  public static JsonException notFound(List<String> jsonProperties) {
    var props = jsonProperties.reduce((a, b) -> a + ", " + b);
    return new JsonException("Non of the [" + props + "] properties were found in the Json string");
  }

  public static JsonException notFoundType(List<Class<?>> types) {
    var classes = types.map(Class::getSimpleName).reduce((a, b) -> a + ", " + b);
    return new JsonException(
            "No Json property implementing any type of [" + classes + "] was found in the Json string");
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
