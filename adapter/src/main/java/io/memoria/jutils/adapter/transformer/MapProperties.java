package io.memoria.jutils.adapter.transformer;

import io.memoria.jutils.core.transformer.Properties;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.ArrayList;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public record MapProperties(Map<String, Object> map) implements Properties {

  public MapProperties(java.util.Map<String, Object> conf) {
    this(HashMap.ofAll(conf));
  }

  @Override
  public Option<Boolean> asBoolean(String key) {
    return asString(key).map(Boolean::parseBoolean);
  }

  @Override
  public List<Boolean> asBooleanList(String key) {
    return asStringList(key).map(l -> l.map(Boolean::parseBoolean));
  }

  @Override
  public Option<Double> asDouble(String key) {
    return asString(key).map(Double::parseDouble);
  }

  @Override
  public List<Double> asDoubleList(String key) {
    return asStringList(key).map(o -> o.map(Double::parseDouble));
  }

  @Override
  public Option<Integer> asInteger(String key) {
    return asString(key).map(Integer::parseInt);
  }

  @Override
  public List<Integer> asIntegerList(String key) {
    return asStringList(key).map(o -> o.map(Integer::parseInt));
  }

  @Override
  public Option<Long> asLong(String key) {
    return asString(key).map(Long::parseLong);
  }

  @Override
  public List<Long> asLongList(String key) {
    return asStringList(key).map(o -> o.map(Long::parseLong));
  }

  @Override
  public Option<String> asString(String key) {
    return map.get(key).map(s -> (String) s);
  }

  @Override
  public List<String> asStringList(String key) {
    var obj = map.get(key);
    if (obj.isDefined()) {
      var result = new ArrayList<String>();
      if (obj.get() instanceof java.util.List<?> list) {
        list.forEach(l -> result.add((String) l));
      }
      return List.ofAll(result);
    } else {
      return List.empty();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Properties sub(String key) {
    return map.get(key).flatMap(m -> {
      if (m instanceof java.util.Map map)
        return some(new MapProperties(map));
      else
        return none();
    });
  }
}
