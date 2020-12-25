package io.memoria.jutils.jconf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JConfUtilsTest {
  @Test
  void appMainCase() {
    var map = JConfUtils.readMainArgs(new String[]{"--config=path/to/file"});
    Assertions.assertEquals("path/to/file", map.get("--config").get());
  }

  @Test
  void readSystemEnvirontment() {
    var map = JConfUtils.readSysEnv();
    Assertions.assertTrue(map.size() > 0);
  }
}
