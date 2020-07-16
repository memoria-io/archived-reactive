package io.memoria.jutils.adapter.file;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static io.memoria.jutils.Tests.reader;
import static io.memoria.jutils.core.file.FileReader.resourcePath;

public class FileReaderTest {

  @Test
  public void fileAsStringTest() {
    var f = reader.file(resourcePath("file.txt").get());
    StepVerifier.create(f).expectNext("hello\nworld").expectComplete().verify();
  }
}
