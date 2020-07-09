package io.memoria.jutils.core.utils.file;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static io.memoria.jutils.Tests.reader;
import static io.memoria.jutils.core.utils.file.ReactiveFileReader.resourcePath;

public class ReactiveFileReaderTest {

  @Test
  public void fileAsStringTest() {
    var f = reader.file(resourcePath("file.txt").get());
    StepVerifier.create(f).expectNext("hello\nworld").expectComplete().verify();
  }
}
