package io.memoria.reactive.core.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RFileTest {
  private static final Logger log = LoggerFactory.getLogger(RFileTest.class.getName());

  @Test
  @DisplayName("Should append or create a file")
  void appendOrCreate() {
    // When
    var writeFileMono = RFile.write(Path.of("target/temp.txt"), "hello world");
    var fileExistsMono = writeFileMono.map(h -> h.toFile().exists());
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
