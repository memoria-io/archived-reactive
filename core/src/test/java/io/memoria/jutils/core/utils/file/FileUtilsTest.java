package io.memoria.jutils.core.utils.file;

import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.nio.file.Paths;

class FileUtilsTest {
  private static final FileUtils fileUtils = new FileUtils(Schedulers.elastic());

  @Test
  void appendOrCreateTest() {
    var helloWorld = fileUtils.write(Path.of("target/temp.txt"), "hello world");
    StepVerifier.create(helloWorld).expectNextCount(1).expectComplete().verify();

    var fileExists = helloWorld.map(h -> h.toFile().exists());
    StepVerifier.create(fileExists).expectNext(true).expectComplete().verify();

    var tempFile = fileUtils.readLines(Paths.get("target/temp.txt"));
    StepVerifier.create(tempFile).expectNext("hello world").expectComplete().verify();
  }

  @Test
  void fileAsStringTest() {
    var f = fileUtils.readResource("file.txt");
    StepVerifier.create(f).expectNext("hello\nworld").expectComplete().verify();
  }
}
