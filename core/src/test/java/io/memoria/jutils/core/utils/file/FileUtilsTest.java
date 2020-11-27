package io.memoria.jutils.core.utils.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUtilsTest {
  private static final FileUtils fu = new FileUtils(Schedulers.boundedElastic());
  private static final String expectedFile = "hello\n\nworld";
  private static final int NUM_OF_LINES = expectedFile.split(lineSeparator()).length;

  @Test
  @DisplayName("Should append or create a file")
  void appendOrCreate() {
    var helloWorld = fu.write(Path.of("target/temp.txt"), "hello world");
    StepVerifier.create(helloWorld).expectNextCount(1).expectComplete().verify();

    var fileExists = helloWorld.map(h -> h.toFile().exists());
    StepVerifier.create(fileExists).expectNext(true).expectComplete().verify();

    var tempFile = fu.readLines(Paths.get("target/temp.txt"));
    StepVerifier.create(tempFile).expectNext("hello world").expectComplete().verify();
  }

  @Test
  @DisplayName("Should read file")
  void readFile() {
    // When
    var file = fu.read(FileUtils.resourcePath("file.txt").get()).block();
    // Then
    assert file != null;
    assertEquals(expectedFile, file);
  }

  @Test
  @DisplayName("Should read file as lines")
  void readFileLines() {
    var flux = fu.readLines(FileUtils.resourcePath("file.txt").get());
    StepVerifier.create(flux).expectNextCount(NUM_OF_LINES).expectComplete().verify();
  }

  @Test
  @DisplayName("should read the nested files")
  void readNestedFile() {
    // When
    var stringMono = fu.read(FileUtils.resourcePath("Config.yaml").get(), "include:");
    var lineFlux = fu.readLines(FileUtils.resourcePath("Config.yaml").get(), "include:");
    // Then
    StepVerifier.create(stringMono).expectNext("name: bob\nage: 20\naddress: 15 bakerstreet").expectComplete().verify();
    StepVerifier.create(lineFlux)
                .expectNext("name: bob", "age: 20", "address: 15 bakerstreet")
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("should read the nested file in resources directory")
  void readNestedResource() {
    // When
    var stringMono = fu.readResource("Config.yaml", "include:");
    var lineFlux = fu.readResourceLines("Config.yaml", "include:");
    // Then
    StepVerifier.create(stringMono).expectNext("name: bob\nage: 20\naddress: 15 bakerstreet").expectComplete().verify();
    StepVerifier.create(lineFlux)
                .expectNext("name: bob", "age: 20", "address: 15 bakerstreet")
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("Should read file in resources directory")
  void readResource() {
    // When
    var file = fu.readResource("file.txt").block();
    // Then
    assert file != null;
    assertEquals(expectedFile, file);
  }

  @Test
  @DisplayName("Should read resource as lines")
  void readResourceLines() {
    var flux = fu.readResourceLines("file.txt");
    StepVerifier.create(flux).expectNextCount(NUM_OF_LINES).expectComplete().verify();
  }

  @Test
  @DisplayName("Should read resource path")
  void resourcePath() {
    assertTrue(FileUtils.resourcePath("file.txt").isSuccess());
    assertTrue(FileUtils.resourcePath("filezzz.txt").isFailure());
  }
}
