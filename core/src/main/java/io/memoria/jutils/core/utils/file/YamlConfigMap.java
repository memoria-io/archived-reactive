package io.memoria.jutils.core.utils.file;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

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

  public Option<Long> asLong(String key) {
    return asString(key).map(Long::parseLong);
  }

  public Option<List<Long>> asLongList(String key) {
    return asStringList(key).map(o -> o.map(Long::parseLong));
  }

  public Option<String> asString(String key) {
    return map.get(key).map(s -> (String) s);
  }

  @SuppressWarnings("unchecked")
  public Option<List<String>> asStringList(String key) {
    return map.get(key).flatMap(m -> {
      if (m instanceof java.util.List)
        return some(List.ofAll((java.util.List<String>) m));
      else
        return none();
    });
  }

  @SuppressWarnings("unchecked")
  public Option<YamlConfigMap> asYamlConfigMap(String key) {
    return map.get(key).flatMap(m -> {
      if (m instanceof java.util.Map)
        return some(new YamlConfigMap((java.util.Map<String, Object>) m));
      else
        return none();
    });
  }
}
