package io.memoria.jutils.jtext.transformer;

import io.vavr.collection.List;

import java.io.IOException;

public class TransformerException extends IOException {
  public static TransformerException noMatchingTransformer(List<Class<?>> types) {
    var classes = types.map(Class::getSimpleName).reduce((a, b) -> a + ", " + b);
    return new TransformerException("No matching transformer found for any of [" + classes + "] ");
  }

  public static <T> TransformerException unsupportedDeserialization(Class<T> type) {
    return new TransformerException("Deserialization is not supported for the type [%s]".formatted(type.getSimpleName()));
  }

  public static <T> TransformerException unsupportedSerialization(Class<T> type) {
    return new TransformerException("Serialization is not supported for the type [%s]".formatted(type.getSimpleName()));
  }

  public TransformerException(String message) {
    super(message);
  }
}
