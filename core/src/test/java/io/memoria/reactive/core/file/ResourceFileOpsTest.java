package io.memoria.reactive.core.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResourceFileOpsTest {
  private static final String TEST_DIR = "file/resourceFileOps/";

  @Test
  void readResource() {
    var str = ResourceFileOps.read(TEST_DIR + "resourceFile.yaml").get();
    Assertions.assertEquals("hello world\nbye bye", str);
  }
}
