package io.memoria.jutils.core;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;

public interface Properties {
  Option<Boolean> asBoolean(String key);

  Option<List<Boolean>> asBooleanList(String key);

  Option<Double> asDouble(String key);

  Option<List<Double>> asDoubleList(String key);

  Option<Integer> asInteger(String key);

  Option<List<Integer>> asIntegerList(String key);

  Option<Long> asLong(String key);

  Option<List<Long>> asLongList(String key);

  Option<String> asString(String key);

  Option<List<String>> asStringList(String key);

  Option<Properties> sub(String key);

  Map<String, Object> map();
}
