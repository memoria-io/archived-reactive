package com.marmoush.jutils.utils.functional;

import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static com.marmoush.jutils.utils.file.FileUtils.writeFile;
import static com.marmoush.jutils.utils.functional.Functional.*;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

public class FunctionalTest {

  @Test
  public void eitherToMonoTest() {
    Either<Exception, Integer> k = right(23);
    Mono<Integer> integerMono = eitherToMono(k);
    StepVerifier.create(integerMono).expectNext(23).expectComplete().verify();

    k = left(new Exception("exception example"));
    integerMono = eitherToMono(k);
    StepVerifier.create(integerMono).expectError().verify();
  }

  @Test
  public void tryToMonoTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Mono<Try<Integer>>> op1 = t -> Mono.just(Try.success((t + " world").length()));
    Function<Integer, Mono<Try<String>>> op2 = t -> Mono.just(Try.success("count is " + t));
    Mono<Try<String>> tryMono = Mono.just(h).flatMap(k -> tryToMonoTry(k, op1)).flatMap(r -> tryToMonoTry(r, op2));
    StepVerifier.create(tryMono).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Mono<Try<String>>> opError = t -> Mono.just(Try.failure(new Exception("should fail")));
    tryMono = tryMono.flatMap(k -> tryToMonoTry(k, opError));
    StepVerifier.create(tryMono).expectNextMatches(t -> t.isFailure()).expectComplete().verify();
  }

  @Test
  public void shorterTryToMonoTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Mono<Try<Integer>>> op1 = t -> Mono.just(Try.success((t + " world").length()));
    Function<Integer, Mono<Try<String>>> op2 = t -> Mono.just(Try.success("count is " + t));
    Mono<Try<String>> tryMono = Mono.just(h).flatMap(tryToMonoTry(op1)).flatMap(tryToMonoTry(op2));
    StepVerifier.create(tryMono).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Mono<Try<String>>> opError = t -> Mono.just(Try.failure(new Exception("should fail")));
    tryMono = tryMono.flatMap(tryToMonoTry(opError));
    StepVerifier.create(tryMono).expectNextMatches(t -> t.isFailure()).expectComplete().verify();
  }

  @Test
  public void tryToMonoVoidTest() {
    Mono<Try<String>> original = Mono.just(Try.success("one"));
    Function<String, Mono<Void>> deferredOp = (String content) -> writeFile("target/one.txt",
                                                                            content,
                                                                            Schedulers.elastic()).then();
    Function<Throwable, Mono<Void>> throwable = t -> Mono.just(Try.failure(new Exception("should not fail"))).then();
    Mono<Void> voidMono = original.flatMap(tryToMonoVoid(deferredOp, throwable));
    StepVerifier.create(voidMono).expectComplete().verify();
  }
}
