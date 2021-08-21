package io.memoria.reactive.core.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppUtilsTest {
  private static final Logger log = LoggerFactory.getLogger(AppUtilsTest.class.getName());

  @Test
  void appMainCase() {
    var map = AppUtils.readMainArgs(new String[]{"--config=path/to/file"});
    Assertions.assertEquals("path/to/file", map.get("--config").get());
  }
}
