package com.marmoush.jutils.utils.yaml;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.net.URI;
import java.util.ArrayList;

public class YamlConfigMap {
  private Map<String, Object> map;

  public YamlConfigMap(java.util.Map<String, Object> conf) {
    map = HashMap.ofAll(conf);
  }

  public String asString(String key) {
    return (String) map.get(key).get();
  }

  public Integer asInt(String key) {
    return (Integer) map.get(key).get();
  }

  public Double asDouble(String key) {
    return (Double) map.get(key).get();
  }

  public URI asURI(String key) {
    return URI.create(asString(key));
  }

  public List<String> asStringList(String key) {
    @SuppressWarnings("unchecked")
    var list = (ArrayList<String>) map.get(key).get();
    return List.ofAll(list);
  }

  public List<Integer> asIntegerList(String key) {
    @SuppressWarnings("unchecked")
    var list = (ArrayList<Integer>) map.get(key).get();
    return List.ofAll(list);
  }

  public List<Double> asDoubleList(String key) {
    @SuppressWarnings("unchecked")
    var list = (ArrayList<Double>) map.get(key).get();
    return List.ofAll(list);
  }

  public List<URI> asURIList(String key) {
    @SuppressWarnings("unchecked")
    var list = (ArrayList<URI>) map.get(key).get();
    return List.ofAll(list);
  }

  public Map<String, String> asMap(String key) {
    @SuppressWarnings("unchecked")
    var m = (java.util.Map<String, String>) map.get(key);
    return HashMap.ofAll(m);
  }
}
