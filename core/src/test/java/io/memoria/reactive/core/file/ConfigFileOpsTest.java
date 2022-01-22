package io.memoria.reactive.core.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ConfigFileOpsTest {
  private static final ConfigFileOps file = new ConfigFileOps("#{include}:", true);

  @ParameterizedTest
  @MethodSource("paths")
  @DisplayName("should read the nested files")
  void readNestedFile(String path) {
    // When
    var file = ConfigFileOpsTest.file.read(path).get();
    // Then
    Assertions.assertEquals("name: bob\nage: 20\naddress: 15 bakerstreet", file);
  }

  private static Stream<String> paths() {
    var path = "Config.yaml";
    var rootPath = ClassLoader.getSystemResource(path).getPath();
    return Stream.of(path, rootPath);
  }
}