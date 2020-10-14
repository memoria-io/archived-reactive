package io.memoria.jutils.adapter.yaml;

import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.core.transformer.Properties;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static io.memoria.jutils.core.transformer.file.FileReader.resourcePath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertiesTest {

  private final Properties configMap = Objects.requireNonNull(Tests.FILE_READER.yaml(resourcePath("yaml/test.yaml").get())
                                                                               .block());

  @Test
  void asNone() {
    assertEquals(Option.none(), configMap.sub("failingMap"));
  }

  @Test
  void asYamlConfigMap() {
    assertEquals("string value", configMap.sub("map").get().asString("key1").get());
  }

  @Test
  void fromBoolean() {
    assertTrue(configMap.asBoolean("booleanValue").get());
  }

  @Test
  void fromBooleanList() {
    assertEquals(List.of(true, false, true), configMap.asBooleanList("booleanList").get());
  }

  @Test
  void fromDoubleList() {
    assertEquals(List.of(1000000000001.23, 1000000000002.23, 1000000000003.23),
                 configMap.asDoubleList("doubleList").get());
  }

  @Test
  void fromIntegerList() {
    assertEquals(List.of(1, 2, 3), configMap.asIntegerList("integerList").get());
  }

  @Test
  void fromLongList() {
    assertEquals(List.of(1000000000001L, 1000000000002L, 1000000000003L), configMap.asLongList("longList").get());
  }

  @Test
  void fromString() {
    assertEquals("hello world", configMap.asString("stringValue").get());
  }

  @Test
  void fromStringList() {
    assertEquals(Option.none(), configMap.asStringList("failingStringList"));
  }

  @Test
  void fromdouble() {
    assertEquals(1000000.000001, configMap.asDouble("doubleValue").get());
  }

  @Test
  void frominteger() {
    assertEquals(10, configMap.asInteger("integerValue").get());
  }

  @Test
  void fromlong() {
    assertEquals(100000000001L, configMap.asLong("longValue").get());
  }

  @Test
  void fromstringList() {
    assertEquals(List.of("hi", "hello", "bye"), configMap.asStringList("stringList").get());
  }

  @Test
  void notNull() {
    assertNotNull(configMap);
  }

  @Test
  @DisplayName("Sub values should be parsed correctly")
  void subValues() {
    Properties map = Tests.FILE_READER.yaml(resourcePath("yaml/main-config.yaml").get()).block();
    assertNotNull(map);
    assertEquals("hello world", map.asString("sub.config.name").get());
    assertEquals("byebye", map.asString("sub.other.value").get());
    assertEquals(List.of("hi", "hello", "bye"), map.asStringList("sub.list").get());
  }
}
