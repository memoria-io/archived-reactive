package io.memoria.reactive.text.jackson.cases.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.memoria.reactive.text.jackson.TestDeps.configs;
import static io.memoria.reactive.text.jackson.TestDeps.yaml;
import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlConfigTest {
  @Test
  @DisplayName("App config nested values should be deserialized correctly")
  void appConfig() {
    String config = configs.read("cases/config/yaml/AppConfigs.yaml").get();
    var appConfig = yaml.deserialize(config, AppConfig.class).block();
    assert appConfig != null;
    assertEquals("hello world", appConfig.subName());
    assertEquals(List.of("hi", "hello", "bye"), appConfig.subList());
  }
}
