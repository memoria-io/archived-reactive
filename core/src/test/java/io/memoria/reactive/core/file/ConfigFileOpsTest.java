package io.memoria.reactive.core.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ConfigFileOpsTest {
  private static final String TEST_DIR = "file/configFileOps/";
  private static final ConfigFileOps configOps = new ConfigFileOps("#{include}:", true);

  @ParameterizedTest
  @MethodSource("paths")
  @DisplayName("should read the nested files")
  void readNestedFile(String path) {
    // When
    var file = configOps.read(path).get();
    // Then
    var expected = ResourceFileOps.readResourceOrFile(TEST_DIR + "expectedConfig.yaml")
                                  .reduce(ConfigFileOps.JOIN_LINES);
    Assertions.assertEquals(expected, file);
  }

  @Test
  void readSystemEnv() {
    // When
    var file = configOps.read(TEST_DIR + "systemEnv.yaml").get();
    // Then
    var expected = ResourceFileOps.readResourceOrFile(TEST_DIR + "expectedSystemEnv.yaml")
                                  .reduce(ConfigFileOps.JOIN_LINES);
    Assertions.assertEquals(expected, file);
  }

  private static Stream<String> paths() {
    var path = TEST_DIR + "Config.yaml";
    var rootPath = ClassLoader.getSystemResource(path).getPath();
    return Stream.of(path, rootPath);
  }
}
