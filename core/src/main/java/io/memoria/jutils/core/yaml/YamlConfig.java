package io.memoria.jutils.core.yaml;

import io.memoria.jutils.adapter.yaml.YamlConfigMap;
import io.vavr.collection.List;
import io.vavr.control.Option;

public interface YamlConfig {
  Option<Boolean> asBoolean(String key);

  Option<List<Boolean>> asBooleanList(String key);

  Option<Double> asDouble(String key);

  Option<List<Double>> asDoubleList(String key);

  Option<Integer> asInteger(String key);

  Option<List<Integer>> asIntegerList(String key);

  Option<Long> asLong(String key);

  Option<List<Long>> asLongList(String key);

  Option<String> asString(String key);

  @SuppressWarnings("unchecked")
  Option<List<String>> asStringList(String key);

  @SuppressWarnings("unchecked")
  Option<YamlConfigMap> asYamlConfigMap(String key);
}
