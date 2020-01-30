package com.marmoush.jutils.utils.functional;

import io.vavr.control.*;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static com.marmoush.jutils.core.utils.file.FileUtils.writeFile;
import static com.marmoush.jutils.core.utils.functional.ReactorVavrUtils.*;
import static io.vavr.control.Either.*;

public class ReactorVavrUtilsTest {
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
    StepVerifier.create(tryMono).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void tryToFluxTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Flux<Try<Integer>>> op1 = t -> Flux.just(Try.success((t + " world").length()));
    Function<Integer, Flux<Try<String>>> op2 = t -> Flux.just(Try.success("count is " + t));
    Flux<Try<String>> tryFlux = Flux.just(h).flatMap(k -> tryToFluxTry(k, op1)).flatMap(r -> tryToFluxTry(r, op2));
    StepVerifier.create(tryFlux).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Flux<Try<String>>> opError = t -> Flux.just(Try.failure(new Exception("should fail")));
    tryFlux = tryFlux.flatMap(k -> tryToFluxTry(k, opError));
    StepVerifier.create(tryFlux).expectNextMatches(Try::isFailure).expectComplete().verify();
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
    StepVerifier.create(tryMono).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void shorterTryToFluxTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Flux<Try<Integer>>> op1 = t -> Flux.just(Try.success((t + " world").length()));
    Function<Integer, Flux<Try<String>>> op2 = t -> Flux.just(Try.success("count is " + t));
    Flux<Try<String>> tryFlux = Flux.just(h).flatMap(tryToFluxTry(op1)).flatMap(tryToFluxTry(op2));
    StepVerifier.create(tryFlux).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Flux<Try<String>>> opError = t -> Flux.just(Try.failure(new Exception("should fail")));
    tryFlux = tryFlux.flatMap(tryToFluxTry(opError));
    StepVerifier.create(tryFlux).expectNextMatches(Try::isFailure).expectComplete().verify();
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

  @Test
  public void eitherToMonoTest() {
    Either<Exception, Integer> k = right(23);
    Mono<Integer> integerMono = eitherToMono(k);
    StepVerifier.create(integerMono).expectNext(23).expectComplete().verify();

    k = left(new Exception("exception example"));
    integerMono = eitherToMono(k);
    StepVerifier.create(integerMono).expectError().verify();
  }
}
