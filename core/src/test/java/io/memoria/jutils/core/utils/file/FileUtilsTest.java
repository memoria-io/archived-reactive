package io.memoria.jutils.core.utils.file;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.nio.file.Path;

import static io.memoria.jutils.core.utils.file.FileUtils.file;
import static io.memoria.jutils.core.utils.file.FileUtils.resource;
import static io.memoria.jutils.core.utils.file.FileUtils.writeFile;

public class FileUtilsTest {

  @Test
  public void fileAsStringTest() {
    StepVerifier.create(resource("file.txt")).expectNext("hello\nworld").expectComplete().verify();
  }

  @Test
  public void appendOrCreateTest() {
    Mono<Path> helloWorld = writeFile("target/temp.txt", "hello world", Schedulers.elastic());
    StepVerifier.create(helloWorld).expectNextCount(1).expectComplete().verify();

    Mono<Boolean> fileExists = helloWorld.map(h -> h.toFile().exists());
    StepVerifier.create(fileExists).expectNext(true).expectComplete().verify();

    Mono<String> tempFile = file("target/temp.txt");
    StepVerifier.create(tempFile).expectNext("hello world").expectComplete().verify();
  }
}
