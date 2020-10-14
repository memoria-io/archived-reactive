package io.memoria.jutils.core.transformer;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;

public interface Properties {
  Option<Boolean> asBoolean(String key);

  List<Boolean> asBooleanList(String key);

  Option<Double> asDouble(String key);

  List<Double> asDoubleList(String key);

  Option<Integer> asInteger(String key);

  List<Integer> asIntegerList(String key);

  Option<Long> asLong(String key);

  List<Long> asLongList(String key);

  Option<String> asString(String key);

  List<String> asStringList(String key);

  Map<String, Object> map();

  Properties sub(String key);
}
