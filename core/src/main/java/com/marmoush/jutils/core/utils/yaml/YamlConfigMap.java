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

  public Boolean asBoolean(String key) {
    return Boolean.parseBoolean(asString(key));
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

  public List<Boolean> asBooleanList(String key) {
    var list = asStringList(key).map(Boolean::parseBoolean);
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

  public Map<String, Object> asMap() {
    return this.map;
  }

  public Map<String, Object> asMap(String key) {
    @SuppressWarnings("unchecked")
    var m = (java.util.Map<String, Object>) map.get(key).get();
    return HashMap.ofAll(m);
  }

  public java.util.Map<String, Object> asJavaMap() {
    return asMap().toJavaMap();
  }

  public java.util.Map<String, Object> asJavaMap(String key) {
    return asMap(key).toJavaMap();
  }

  public YamlConfigMap asYamlConfigMap(String key) {
    @SuppressWarnings("unchecked")
    var m = (java.util.Map<String, Object>) map.get(key).get();
    return new YamlConfigMap(m);
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
