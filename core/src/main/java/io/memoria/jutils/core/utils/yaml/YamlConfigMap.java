package io.memoria.jutils.core.utils.yaml;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import java.util.ArrayList;

public record YamlConfigMap(Map<String, Object>map) {

  public YamlConfigMap(java.util.Map<String, Object> conf) {
    this(HashMap.ofAll(conf));
  }

  public Option<Boolean> asBoolean(String key) {
    return asString(key).map(Boolean::parseBoolean);
  }

  public Option<List<Boolean>> asBooleanList(String key) {
    return asStringList(key).map(l -> l.map(Boolean::parseBoolean));
  }

  public Option<Double> asDouble(String key) {
    return asString(key).map(Double::parseDouble);
  }

  public Option<List<Double>> asDoubleList(String key) {
    return asStringList(key).map(o -> o.map(Double::parseDouble));
  }

  public Option<Integer> asInteger(String key) {
    return asString(key).map(Integer::parseInt);
  }

  public Option<List<Integer>> asIntegerList(String key) {
    return asStringList(key).map(o -> o.map(Integer::parseInt));
  }

  public java.util.Map<String, Object> asJavaMap() {
    return asMap().toJavaMap();
  }

  public Option<java.util.Map<String, Object>> asJavaMap(String key) {
    return asMap(key).map(Map::toJavaMap);
  }

  public Option<Long> asLong(String key) {
    return asString(key).map(Long::parseLong);
  }

  public Option<List<Long>> asLongList(String key) {
    return asStringList(key).map(o -> o.map(Long::parseLong));
  }

  public Map<String, Object> asMap() {
    return this.map;
  }

  public Option<Map<String, Object>> asMap(String key) {
    //noinspection unchecked
    return map.get(key).map(m -> (java.util.Map<String, Object>) m).map(HashMap::ofAll);
  }

  public Option<String> asString(String key) {
    return map.get(key).map(s -> (String) s);
  }

  public Option<List<String>> asStringList(String key) {
    //noinspection unchecked
    return map.get(key).map(l -> (ArrayList<String>) l).map(List::ofAll);
  }

  public Option<YamlConfigMap> asYamlConfigMap(String key) {
    //noinspection unchecked
    return map.get(key).map(m -> (java.util.Map<String, Object>) m).map(YamlConfigMap::new);
  }
}
