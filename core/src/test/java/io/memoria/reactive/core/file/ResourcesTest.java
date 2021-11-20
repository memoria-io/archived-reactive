package io.memoria.reactive.core.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResourcesTest {
  @Test
  void readResource() {
    var str = Resources.read("subconfig.yaml").get();
    Assertions.assertEquals("age: 20", str);
  }
}
