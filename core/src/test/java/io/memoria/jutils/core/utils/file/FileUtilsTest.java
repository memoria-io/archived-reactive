package io.memoria.jutils.core.utils.file;

import io.memoria.jutils.core.utils.functional.ReactorVavrUtils;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.nio.file.Path;

import static io.memoria.jutils.core.utils.file.FileUtils.writeFile;

public class FileUtilsTest {
  @Test
  public void fileAsStringTest() {
    Assertions.assertEquals("hello\nworld", FileUtils.resource("file.txt").get());
  }

  @Test
  public void appendOrCreateTest() {
    Mono<Try<Path>> helloWorld = FileUtils.writeFile("target/temp.txt", "hello world", Schedulers.elastic());
    StepVerifier.create(helloWorld).expectNextMatches(Try::isSuccess).expectComplete().verify();
    Mono<Try<Boolean>> fileExists = helloWorld.flatMap(ReactorVavrUtils.tryToMonoTry(h -> Mono.just(Try.of(() -> h.toFile()
                                                                                                                  .exists()))));
    StepVerifier.create(fileExists).expectNext(Try.success(true)).expectComplete().verify();

    String tempFile = FileUtils.file("target/temp.txt").get();
    Assertions.assertEquals("hello world", tempFile);
  }
}
