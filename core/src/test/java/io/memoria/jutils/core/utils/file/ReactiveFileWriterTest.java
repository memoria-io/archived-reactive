package io.memoria.jutils.core.utils.file;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.memoria.jutils.Tests.reader;
import static io.memoria.jutils.Tests.writer;

public class ReactiveFileWriterTest {

  @Test
  public void appendOrCreateTest() {
    Mono<Path> helloWorld = writer.writeFile(Paths.get("target/temp.txt"), "hello world");
    StepVerifier.create(helloWorld).expectNextCount(1).expectComplete().verify();

    Mono<Boolean> fileExists = helloWorld.map(h -> h.toFile().exists());
    StepVerifier.create(fileExists).expectNext(true).expectComplete().verify();

    Mono<String> tempFile = reader.file(Paths.get("target/temp.txt"));
    StepVerifier.create(tempFile).expectNext("hello world").expectComplete().verify();
  }
}
