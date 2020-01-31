package com.marmoush.jutils.core.utils.yaml;

import io.vavr.collection.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YamlUtilsTest {

  @Test
  void parseYamlFile() {
    YamlConfigMap resource = YamlUtils.parseYamlResource("utils/test.yaml").get();
    YamlConfigMap file = YamlUtils.parseYamlFile("src/test/resources/utils/test.yaml", false).get();
    assertEquals(resource, file);
  }

  @Test
  @DisplayName("Values should be parsed correctly")
  public void values() {
    YamlConfigMap map = YamlUtils.parseYamlResource("utils/test.yaml").get();
    // string
    assertEquals("hello world", map.asString("stringValue"));
    // integer
    assertEquals(10, map.asInteger("integerValue"));
    // long
    assertEquals(100000000001L, map.asLong("longValue"));
    // double
    assertEquals(1000000.000001, map.asDouble("doubleValue"));
    // string list
    assertEquals(List.of("hi", "hello", "bye"), map.asStringList("stringList"));
    // integer list
    assertEquals(List.of(1, 2, 3), map.asIntegerList("integerList"));
    // long list
    assertEquals(List.of(1000000000001L, 1000000000002L, 1000000000003L), map.asLongList("longList"));
    // double list
    assertEquals(List.of(1000000000001.23, 1000000000002.23, 1000000000003.23), map.asDoubleList("doubleList"));
    // map
    //noinspection AssertEqualsBetweenInconvertibleTypes
    assertEquals(HashMap.of("key1", "string value", "key2", "2").toJavaMap(), map.asJavaMap("map"));
  }

  @Test
  @DisplayName("Subvalues should be parsed correctly")
  void subValues() {
    YamlConfigMap map = YamlUtils.parseYamlResource("utils/main-config.yaml").get();
    assertEquals("hello world", map.asString("sub.config.name"));
    assertEquals("byebye", map.asString("sub.other.value"));
    assertEquals(List.of("hi", "hello", "bye"), map.asStringList("sub.list"));
  }
}
