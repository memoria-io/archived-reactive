package io.memoria.jutils.jconf;

import io.memoria.jutils.core.utils.file.FileUtils;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;

class JConfTest {
  private static record Config(String first, String second, int third, String invalid) {}

  private static final String CONFIG_YAML = "config.yaml";

  @Test
  void property() {
    var result = JConfUtils.resolveExpression("${hello:-hi:-asdf", HashMap.empty());
    Assertions.assertEquals(Option.none(), result);
  }

  @Test
  void readFromFile() {
    // Given
    var jconf = JConf.build(HashMap.empty(), Schedulers.boundedElastic());
    // When
    var configs = jconf.read(FileUtils.resourcePath(CONFIG_YAML).get(), Config.class).block();
    // Then
    Assertions.assertNotNull(configs);
    Assertions.assertEquals("defaultValue", configs.first);
    Assertions.assertEquals("${second}", configs.second);
    Assertions.assertEquals(500, configs.third);
    Assertions.assertEquals("${heyy:--bye}", configs.invalid);
  }

  @Test
  void readFromResource() {
    // Given
    var jconf = JConf.build(HashMap.empty(),Schedulers.boundedElastic());
    // When
    var configs = jconf.readResource(CONFIG_YAML, Config.class).block();
    // Then
    Assertions.assertNotNull(configs);
    Assertions.assertEquals("defaultValue", configs.first);
    Assertions.assertEquals("${second}", configs.second);
    Assertions.assertEquals(500, configs.third);
    Assertions.assertEquals("${heyy:--bye}", configs.invalid);
  }

  @Test
  void readProperty() {
    // Given
    var map = HashMap.of("FIRST_VAR", "some value");
    var jconf = JConf.build(map,Schedulers.boundedElastic());
    // When
    var configs = jconf.readResource(CONFIG_YAML, Config.class).block();
    // Then
    Assertions.assertNotNull(configs);
    Assertions.assertEquals("some value", configs.first);
    Assertions.assertEquals("${second}", configs.second);
    Assertions.assertEquals(500, configs.third);
  }
}
