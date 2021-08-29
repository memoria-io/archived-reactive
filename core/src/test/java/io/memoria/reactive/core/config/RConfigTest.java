package io.memoria.reactive.core.config;

import io.memoria.reactive.core.config.RConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

class RConfigTest {
  private static final RConfig file = new RConfig("#{include}:", true);

  @ParameterizedTest
  @MethodSource("paths")
  @DisplayName("should read the nested files")
  void readNestedFile(String path) {
    // When
    var stringMono = file.read(path);
    // Then
    StepVerifier.create(stringMono).expectNext("name: bob\nage: 20\naddress: 15 bakerstreet").expectComplete().verify();
  }

  private static Stream<String> paths() {
    var path = "Config.yaml";
    var rootPath = ClassLoader.getSystemResource(path).getPath();
    return Stream.of(path, rootPath);
  }
}
