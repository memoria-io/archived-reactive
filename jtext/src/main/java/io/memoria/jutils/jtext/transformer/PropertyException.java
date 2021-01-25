package io.memoria.jutils.jtext.transformer;

import io.vavr.collection.List;

public class PropertyException extends TransformerException {
  public static PropertyException noMatchingAdapter(String property) {
    return new PropertyException("Property [%s] has no matching adapter".formatted(property));
  }

  public static PropertyException notFound(String property) {
    return new PropertyException("Property [%s] was not found in the string".formatted(property));
  }

  public static PropertyException notFound(List<String> property) {
    var props = property.reduce((a, b) -> a + ", " + b);
    return new PropertyException("Non of the [" + props + "] properties were found in the string");
  }

  public static PropertyException unknown(String property) {
    return new PropertyException("Property [%s] is unknown".formatted(property));
  }

  public PropertyException(String message) {
    super(message);
  }
}
