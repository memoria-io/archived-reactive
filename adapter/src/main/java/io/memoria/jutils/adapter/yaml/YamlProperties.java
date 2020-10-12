package io.memoria.jutils.adapter.yaml;

import io.memoria.jutils.core.Properties;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public record YamlProperties(Map<String, Object> map) implements Properties {

  public YamlProperties(java.util.Map<String, Object> conf) {
    this(HashMap.ofAll(conf));
  }

  @Override
  public Option<Boolean> asBoolean(String key) {
    return asString(key).map(Boolean::parseBoolean);
  }

  @Override
  public Option<List<Boolean>> asBooleanList(String key) {
    return asStringList(key).map(l -> l.map(Boolean::parseBoolean));
  }

  @Override
  public Option<Double> asDouble(String key) {
    return asString(key).map(Double::parseDouble);
  }

  @Override
  public Option<List<Double>> asDoubleList(String key) {
    return asStringList(key).map(o -> o.map(Double::parseDouble));
  }

  @Override
  public Option<Integer> asInteger(String key) {
    return asString(key).map(Integer::parseInt);
  }

  @Override
  public Option<List<Integer>> asIntegerList(String key) {
    return asStringList(key).map(o -> o.map(Integer::parseInt));
  }

  @Override
  public Option<Long> asLong(String key) {
    return asString(key).map(Long::parseLong);
  }

  @Override
  public Option<List<Long>> asLongList(String key) {
    return asStringList(key).map(o -> o.map(Long::parseLong));
  }

  @Override
  public Option<String> asString(String key) {
    return map.get(key).map(s -> (String) s);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Option<List<String>> asStringList(String key) {
    return map.get(key).flatMap(m -> {
      if (m instanceof java.util.List)
        return some(List.ofAll((java.util.List<String>) m));
      else
        return none();
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public Option<Properties> sub(String key) {
    return map.get(key).flatMap(m -> {
      if (m instanceof java.util.Map)
        return some(new YamlProperties((java.util.Map<String, Object>) m));
      else
        return none();
    });
  }
}
