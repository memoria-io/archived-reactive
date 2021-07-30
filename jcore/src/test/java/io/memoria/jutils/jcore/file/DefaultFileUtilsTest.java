package io.memoria.jutils.jcore.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultFileUtilsTest {
  private static final Logger log = LoggerFactory.getLogger(DefaultFileUtilsTest.class.getName());

  private static final FileUtils file = FileUtils.createDefault("#{include}:", true, Schedulers.boundedElastic());

  @Test
  @DisplayName("Should append or create a file")
  void appendOrCreate() {
    // When
    var writeFileMono = file.write(Path.of("target/temp.txt"), "hello world");
    var fileExistsMono = writeFileMono.map(h -> h.toFile().exists());
    // Then
    StepVerifier.create(writeFileMono).expectNextCount(1).expectComplete().verify();
    StepVerifier.create(fileExistsMono).expectNext(true).expectComplete().verify();
  }

  @Test
  @DisplayName("Should read file as resources file if it's relative path")
  void readFile() throws IOException {
    var is = FileUtils.inputStream("Config.yaml").get();
    var expected = is.readAllBytes();
    var actual = Files.readAllBytes(Path.of(ClassLoader.getSystemResource("Config.yaml").getPath()));
    assertEquals(new String(expected), new String(actual));
  }

  @Test
  void readFileWithSystemEnv() {
    var javaHome = System.getenv("JAVA_HOME");
    if (javaHome != null && !javaHome.isEmpty()) {
      var fileMono = file.readLines("SystemEnv.yaml");
      StepVerifier.create(fileMono)
                  .expectNextMatches(s -> !s.contains("${"))
                  .expectNext("otherValue: defaultValue")
                  .expectComplete()
                  .verify();
    } else {
      log.warn("Test skipped, couldn't read the system environment variable JAVA_HOME");
    }
  }

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
