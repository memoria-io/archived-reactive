package io.memoria.reactive.core.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConfigUtilsTest {
  @Test
  void appMainCase() {
    var map = ConfigUtils.readMainArgs(new String[]{"--config=path/to/file"});
    Assertions.assertEquals("path/to/file", map.get("--config").get());
  }
}
