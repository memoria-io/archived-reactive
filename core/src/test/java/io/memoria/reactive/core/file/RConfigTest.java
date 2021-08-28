package io.memoria.reactive.core.file;

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
    var lineFlux = file.readLines(path);
    // Then
    StepVerifier.create(stringMono).expectNext("name: bob\nage: 20\naddress: 15 bakerstreet").expectComplete().verify();
    StepVerifier.create(lineFlux).expectNextCount(3).expectComplete().verify();
    StepVerifier.create(lineFlux)
                .expectNext("name: bob")
                .expectNext("age: 20")
                .expectNext("address: 15 bakerstreet")
                .expectComplete()
                .verify();
  }

  private static Stream<String> paths() {
    var path = "Config.yaml";
    var rootPath = ClassLoader.getSystemResource(path).getPath();
    return Stream.of(path, rootPath);
  }
}
