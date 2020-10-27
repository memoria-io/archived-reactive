package io.memoria.jutils.jackson.transformer.yaml;

import io.memoria.jutils.jackson.Tests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlConfigTest {
  @Test
  @DisplayName("App config nested values should be deserialized correctly")
  void appConfig() {
    var appConfig = Tests.yaml.deserialize(Tests.APP_CONFIG_YAML, AppConfig.class).get();
    assertEquals("hello world", appConfig.subName());
    assertEquals(List.of("hi", "hello", "bye"), appConfig.subList());
  }
}
