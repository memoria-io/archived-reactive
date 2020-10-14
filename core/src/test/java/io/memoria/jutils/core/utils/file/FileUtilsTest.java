package io.memoria.jutils.core.utils.file;

import io.memoria.jutils.adapter.Tests;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.nio.file.Paths;

import static io.memoria.jutils.core.transformer.file.FileReader.resourcePath;

class FileUtilsTest {

  @Test
  void fileAsStringTest() {
    var f = Tests.FILE_READER.file(resourcePath("file.txt").get());
    StepVerifier.create(f).expectNext("hello\nworld").expectComplete().verify();
  }
  @Test
  void appendOrCreateTest() {
    var helloWorld = Tests.FILE_WRITER.writeFile(Paths.get("target/temp.txt"), "hello world");
    StepVerifier.create(helloWorld).expectNextCount(1).expectComplete().verify();

    var fileExists = helloWorld.map(h -> h.toFile().exists());
    StepVerifier.create(fileExists).expectNext(true).expectComplete().verify();

    var tempFile = Tests.FILE_READER.file(Paths.get("target/temp.txt"));
    StepVerifier.create(tempFile).expectNext("hello world").expectComplete().verify();
  }
}
