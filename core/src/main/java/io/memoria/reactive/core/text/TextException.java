package io.memoria.reactive.core.text;

import io.vavr.collection.List;

import java.io.IOException;

public class TextException extends IOException {
  public static TextException noMatchingAdapter(String property) {
    return new TextException("Property [%s] has no matching adapter".formatted(property));
  }

  public static TextException noMatchingTransformer(List<Class<?>> types) {
    var classes = types.map(Class::getSimpleName).reduce((a, b) -> a + ", " + b);
    return new TextException("No matching transformer found for any of [" + classes + "] ");
  }

  public static TextException notFound(List<String> property) {
    var props = property.reduce((a, b) -> a + ", " + b);
    return new TextException("Non of the [" + props + "] properties were found in the string");
  }

  public static TextException notFound(String property) {
    return new TextException("Property [%s] was not found in the string".formatted(property));
  }

  public static TextException unknown(String property) {
    return new TextException("Property [%s] is unknown".formatted(property));
  }

  public static <T> TextException unsupportedDeserialization(Class<T> type) {
    return new TextException("Deserialization is not supported for the type [%s]".formatted(type.getSimpleName()));
  }

  public static <T> TextException unsupportedSerialization(Class<T> type) {
    return new TextException("Serialization is not supported for the type [%s]".formatted(type.getSimpleName()));
  }

  public TextException(String message) {
    super(message);
  }
}
