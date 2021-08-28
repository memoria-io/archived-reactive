package io.memoria.reactive.core.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RAppTest {

  @Test
  void appMainCase() {
    var map = RApp.readMainArgs(new String[]{"--config=path/to/file"});
    Assertions.assertEquals("path/to/file", map.get("--config").get());
  }
}
