package io.memoria.jutils.adapter.yaml;

import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.core.yaml.Yaml;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static io.memoria.jutils.core.file.FileReader.resourcePath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YamlTest {

  private final Yaml configMap = Objects.requireNonNull(Tests.FILE_READER.yaml(resourcePath("utils/test.yaml").get())
                                                                         .block());

  @Test
  public void asNone() {
    assertEquals(Option.none(), configMap.asYaml("failingMap"));
  }

  @Test
  public void asYamlConfigMap() {
    assertEquals("string value", configMap.asYaml("map").get().asString("key1").get());
  }

  @Test
  public void fromBoolean() {
    assertTrue(configMap.asBoolean("booleanValue").get());
  }

  @Test
  public void fromBooleanList() {
    assertEquals(List.of(true, false, true), configMap.asBooleanList("booleanList").get());
  }

  @Test
  public void fromDoubleList() {
    assertEquals(List.of(1000000000001.23, 1000000000002.23, 1000000000003.23),
                 configMap.asDoubleList("doubleList").get());
  }

  @Test
  public void fromIntegerList() {
    assertEquals(List.of(1, 2, 3), configMap.asIntegerList("integerList").get());
  }

  @Test
  public void fromLongList() {
    assertEquals(List.of(1000000000001L, 1000000000002L, 1000000000003L), configMap.asLongList("longList").get());
  }

  @Test
  public void fromString() {
    assertEquals("hello world", configMap.asString("stringValue").get());
  }

  @Test
  public void fromStringList() {
    assertEquals(Option.none(), configMap.asStringList("failingStringList"));
  }

  @Test
  public void fromdouble() {
    assertEquals(1000000.000001, configMap.asDouble("doubleValue").get());
  }

  @Test
  public void frominteger() {
    assertEquals(10, configMap.asInteger("integerValue").get());
  }

  @Test
  public void fromlong() {
    assertEquals(100000000001L, configMap.asLong("longValue").get());
  }

  @Test
  public void fromstringList() {
    assertEquals(List.of("hi", "hello", "bye"), configMap.asStringList("stringList").get());
  }

  @Test
  public void notNull() {
    assertNotNull(configMap);
  }

  @Test
  @DisplayName("Sub values should be parsed correctly")
  public void subValues() {
    Yaml map = Tests.FILE_READER.yaml(resourcePath("utils/main-config.yaml").get()).block();
    assertNotNull(map);
    assertEquals("hello world", map.asString("sub.config.name").get());
    assertEquals("byebye", map.asString("sub.other.value").get());
    assertEquals(List.of("hi", "hello", "bye"), map.asStringList("sub.list").get());
  }
}
