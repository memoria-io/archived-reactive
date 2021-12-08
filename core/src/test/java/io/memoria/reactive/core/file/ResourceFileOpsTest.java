package io.memoria.reactive.core.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResourceFileOpsTest {
  @Test
  void readResource() {
    var str = ResourceFileOps.read("subconfig.yaml").get();
    Assertions.assertEquals("age: 20", str);
  }
}
