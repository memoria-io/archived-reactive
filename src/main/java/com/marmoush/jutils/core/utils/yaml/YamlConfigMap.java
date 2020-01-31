package com.marmoush.jutils.core.utils.yaml;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.util.*;

public class YamlConfigMap {
  private Map<String, Object> map;

  public YamlConfigMap(java.util.Map<String, Object> conf) {
    map = HashMap.ofAll(conf);
  }

  public String asString(String key) {
    return (String) map.get(key).get();
  }

  public Integer asInteger(String key) {
    return Integer.parseInt(asString(key));
  }

  public Long asLong(String key) {
    return Long.parseLong(asString(key));
  }

  public Double asDouble(String key) {
    return Double.parseDouble(asString(key));
  }

  public List<String> asStringList(String key) {
    @SuppressWarnings("unchecked")
    var list = (ArrayList<String>) map.get(key).get();
    return List.ofAll(list);
  }

  public List<Integer> asIntegerList(String key) {
    return List.ofAll(asStringList(key)).map(Integer::parseInt);
  }

  public List<Long> asLongList(String key) {
    return List.ofAll(asStringList(key)).map(Long::parseLong);
  }

  public List<Double> asDoubleList(String key) {
    return List.ofAll(asStringList(key)).map(Double::parseDouble);
  }

  public YamlConfigMap asMap(String key) {
    @SuppressWarnings("unchecked")
    var m = (java.util.Map<String, Object>) map.get(key).get();
    return new YamlConfigMap(m);
  }

  public java.util.Map<String, Object> asJavaMap(String key) {
    @SuppressWarnings("unchecked")
    var m = (java.util.Map<String, Object>) map.get(key).get();
    return HashMap.ofAll(m).toJavaMap();
  }

  public java.util.Map<String, Object> toJavaMap() {
    return this.map.toJavaMap();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    YamlConfigMap that = (YamlConfigMap) o;
    return map.equals(that.map);
  }

  @Override
  public int hashCode() {
    return Objects.hash(map);
  }
}
