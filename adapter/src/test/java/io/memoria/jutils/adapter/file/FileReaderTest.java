package io.memoria.jutils.adapter.file;

import io.memoria.jutils.adapter.Tests;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static io.memoria.jutils.core.file.FileReader.resourcePath;

public class FileReaderTest {

  @Test
  public void fileAsStringTest() {
    var f = Tests.FILE_READER.file(resourcePath("file.txt").get());
    StepVerifier.create(f).expectNext("hello\nworld").expectComplete().verify();
  }
}
