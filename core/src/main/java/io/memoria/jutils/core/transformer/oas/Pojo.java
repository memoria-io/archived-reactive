package io.memoria.jutils.core.transformer.oas;

import java.util.Map;

public sealed interface Pojo {
  record PojoArray(Pojo type) implements Pojo {}

  record PojoBoolean() implements Pojo {}

  record PojoFloat() implements Pojo {}

  record PojoDouble() implements Pojo {}

  record PojoInteger() implements Pojo {}

  record PojoLong() implements Pojo {}

  record PojoObject(Map<String, Pojo> properties) implements Pojo {}

  /**
   * Key type is string in JSON
   */
  record PojoMap(Pojo valuesType) implements Pojo {}

  record PojoString() implements Pojo {}
}
