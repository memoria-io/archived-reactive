package io.memoria.jutils.core.utils.yaml;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.memoria.jutils.Tests.YAML_FILE_PARSER;
import static io.memoria.jutils.Tests.YAML_RESOURCE_PARSER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YamlUtilsTest {

  @Test
  @DisplayName("Reading same file should produce same map")
  public void sameFileTest() {
    YamlConfigMap map = YAML_RESOURCE_PARSER.apply("utils/test.yaml").block();
    YamlConfigMap map2 = YAML_RESOURCE_PARSER.apply("utils/test.yaml").block();

    assertEquals(map, map2);
  }

  @Test
  @DisplayName("Sub values should be parsed correctly")
  public void subValues() {
    YamlConfigMap map = YAML_RESOURCE_PARSER.apply("utils/main-config.yaml").block();
    assertNotNull(map);
    assertEquals("hello world", map.asString("sub.config.name").get());
    assertEquals("byebye", map.asString("sub.other.value").get());
    assertEquals(List.of("hi", "hello", "bye"), map.asStringList("sub.list").get());
  }

  @Test
  @DisplayName("Values should be parsed correctly")
  public void values() {
    YamlConfigMap map = YAML_RESOURCE_PARSER.apply("utils/test.yaml").block();
    assertNotNull(map);
    // string
    assertEquals("hello world", map.asString("stringValue").get());

    // boolean
    assertTrue(map.asBoolean("booleanValue").get());

    // integer
    assertEquals(10, map.asInteger("integerValue").get());

    // long
    assertEquals(100000000001L, map.asLong("longValue").get());

    // double
    assertEquals(1000000.000001, map.asDouble("doubleValue").get());

    // string list
    assertEquals(List.of("hi", "hello", "bye"), map.asStringList("stringList").get());

    // string list
    assertEquals(Option.none(), map.asStringList("failingStringList"));

    // boolean list
    assertEquals(List.of(true, false, true), map.asBooleanList("booleanList").get());

    // integer list
    assertEquals(List.of(1, 2, 3), map.asIntegerList("integerList").get());

    // long list
    assertEquals(List.of(1000000000001L, 1000000000002L, 1000000000003L), map.asLongList("longList").get());

    // double list
    assertEquals(List.of(1000000000001.23, 1000000000002.23, 1000000000003.23), map.asDoubleList("doubleList").get());

    // vavr map
    assertEquals(HashMap.of("key1", "string value", "key2", "2"), map.asMap("map").get());
    assertEquals("hello world", map.asMap().get("stringValue").get());

    // asYamlConfigMap
    assertEquals(Option.none(), map.asMap("failingMap"));

    // asYamlConfigMap
    assertEquals("string value", map.asYamlConfigMap("map").get().asString("key1").get());

    // asYamlConfigMap
    assertEquals(Option.none(), map.asYamlConfigMap("failingMap"));

    // as java map
    //noinspection AssertEqualsBetweenInconvertibleTypes
    assertEquals(HashMap.of("key1", "string value", "key2", "2").toJavaMap(), map.asJavaMap("map").get());
    assertEquals(HashMap.of("key1", "string value", "key2", "2").toJavaMap(), map.asJavaMap().get("map"));
  }

  @Test
  void parseYamlFile() {
    YamlConfigMap resource = YAML_RESOURCE_PARSER.apply("utils/test.yaml").block();
    YamlConfigMap file = YAML_FILE_PARSER.apply("src/test/resources/utils/test.yaml").block();
    assertEquals(resource, file);
  }
}
