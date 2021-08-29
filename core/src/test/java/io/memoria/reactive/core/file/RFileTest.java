package io.memoria.reactive.core.file;

import io.vavr.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RFileTest {
  private static final Logger log = LoggerFactory.getLogger(RFileTest.class.getName());
  private static final String reactiveDir = "/tmp/reactive";

  @BeforeEach
  void beforeEach() {
    RFile.clean(reactiveDir).subscribe();
  }

  @Test
  void watch() {
    var pub = RFile.publish(reactiveDir,
                            Flux.just(Tuple.of("f1", "hello"), Tuple.of("f2", "hi"), Tuple.of("f3", "bye")));
    StepVerifier.create(pub).expectNextCount(3).verifyComplete();
    var sub = RFile.subscribe("/tmp/reactive", 0).take(3).subscribe(System.out::println);
  }

  @Test
  @DisplayName("Should append or create a file")
  void appendOrCreate() {
    // When
    var filePath = "target/temp.txt";
    var writeFileMono = RFile.write(filePath, "hello world");
    var fileExistsMono = writeFileMono.map(h -> Path.of(filePath).toFile().exists());
    // Then
    StepVerifier.create(writeFileMono).expectNextCount(1).expectComplete().verify();
    StepVerifier.create(fileExistsMono).expectNext(true).expectComplete().verify();
  }

  @Test
  @DisplayName("Should read file as resources file if it's relative path")
  void readFile() throws IOException {
    var is = RFile.inputStream("Config.yaml").get();
    var expected = is.readAllBytes();
    var actual = Files.readAllBytes(Path.of(ClassLoader.getSystemResource("Config.yaml").getPath()));
    assertEquals(new String(expected), new String(actual));
  }
}
