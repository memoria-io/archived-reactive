package io.memoria.jutils.core.utils.file;

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
  private static final String expectedFile = """
          hello
                    
          world
          """;
  private static final int NUM_OF_LINES = expectedFile.split(lineSeparator()).length;

  @Test
  void appendOrCreateTest() {
    var helloWorld = fu.write(Path.of("target/temp.txt"), "hello world");
    StepVerifier.create(helloWorld).expectNextCount(1).expectComplete().verify();

    var fileExists = helloWorld.map(h -> h.toFile().exists());
    StepVerifier.create(fileExists).expectNext(true).expectComplete().verify();

    var tempFile = fu.readLines(Paths.get("target/temp.txt"));
    StepVerifier.create(tempFile).expectNext("hello world").expectComplete().verify();
  }

  @Test
  void should_read_nested_documents() {
    // When
    var stringMono = fu.readResource("Config.yaml", "include:");
    var lineFlux = fu.readResourceLines("Config.yaml", "include:");
    // Then
    StepVerifier.create(stringMono).expectNext("name: bob\nage: 20").expectComplete().verify();
    StepVerifier.create(lineFlux).expectNext("name: bob").expectNext("age: 20").expectComplete().verify();
  }

  @Test
  void should_read_resource() {
    // When
    var file = fu.readResource("file.txt").block();
    // Then
    assert file != null;
    System.out.println(file.split(lineSeparator()).length);
    assertEquals(expectedFile, file);
  }

  @Test
  void should_read_resource_in_lines() {
    var flux = fu.readResourceLines("file.txt");
    StepVerifier.create(flux).expectNextCount(NUM_OF_LINES).expectComplete().verify();
  }

  @Test
  void should_read_resource_path() {
    assertTrue(FileUtils.resourcePath("file.txt").isSuccess());
    assertTrue(FileUtils.resourcePath("filezzz.txt").isFailure());
  }
}
