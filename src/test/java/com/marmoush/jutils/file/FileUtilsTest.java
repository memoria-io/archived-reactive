package com.marmoush.jutils.file;

import com.marmoush.jutils.functional.Functional;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.marmoush.jutils.file.FileUtils.*;
import static java.util.function.UnaryOperator.identity;

public class FileUtilsTest {
  @Test
  public void fileAsStringTest() throws IOException {
    String actual = FileUtils.asStringBlocking(resource("file.txt"), identity());
    Assertions.assertEquals("hello\nworld", actual);
  }

  @Test
  public void asFileTest() {
    Try<File> file = asFile("file.txt");
    Assertions.assertTrue(file.isSuccess());
    Assertions.assertTrue(file.get().canRead());
  }

  @Test
  public void appendOrCreateTest() {
    Mono<Try<Path>> helloWorld = writeFile("target/temp.txt", "hello world", Schedulers.elastic());
    StepVerifier.create(helloWorld).expectNextMatches(t -> t.isSuccess()).expectComplete().verify();
    Mono<Try<Boolean>> fileExists = helloWorld.flatMap(Functional.tryToMonoTry(h -> Mono.just(Try.of(() -> h.toFile()
                                                                                                            .exists()))));
    StepVerifier.create(fileExists).expectNext(Try.success(true)).expectComplete().verify();

    Mono<String> stringMono = asString(file("target/temp.txt").get(), Schedulers.elastic());
    StepVerifier.create(stringMono).expectNext("hello world").expectComplete().verify();
  }
}
