package io.memoria.jutils.core.utils.functional;

import io.memoria.jutils.core.utils.file.FileUtils;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.blockingToVoidMono;
import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.checkedMono;
import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.tryToMono;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

public class ReactorVavrUtilsTest {
  @Test
  public void blockingToMono() {
    class ThreadName {
      public String threadName;
    }
    final ThreadName n = new ThreadName();
    var m = blockingToVoidMono(() -> {
      Thread.sleep(1000);
      n.threadName = Thread.currentThread().getName();
    }, Schedulers.elastic());
    Assertions.assertNull(n.threadName);
    m.block();
    Assertions.assertNotNull(n.threadName);
    Assertions.assertTrue(n.threadName.contains("elastic"));
  }

  @Test
  public void checkedMonoTest() {
    AtomicBoolean b = new AtomicBoolean();
    var m = checkedMono(() -> {
      Thread.sleep(1000);
      b.getAndSet(true);
    });
    // Making sure mono isn't executed
    Assertions.assertFalse(b.get());
    // Now should be executed
    m.block();
    Assertions.assertTrue(b.get());
  }

  @Test
  public void eitherToMonoTest() {
    Either<Exception, Integer> k = right(23);
    Mono<Integer> integerMono = ReactorVavrUtils.eitherToMono(k);
    StepVerifier.create(integerMono).expectNext(23).expectComplete().verify();

    k = left(new Exception("exception example"));
    integerMono = ReactorVavrUtils.eitherToMono(k);
    StepVerifier.create(integerMono).expectError().verify();
  }

  @Test
  public void shorterTryToFluxTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Flux<Try<Integer>>> op1 = t -> Flux.just(Try.success((t + " world").length()));
    Function<Integer, Flux<Try<String>>> op2 = t -> Flux.just(Try.success("count is " + t));
    Flux<Try<String>> tryFlux = Flux.just(h)
                                    .flatMap(ReactorVavrUtils.tryToFluxTry(op1))
                                    .flatMap(ReactorVavrUtils.tryToFluxTry(op2));
    StepVerifier.create(tryFlux).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Flux<Try<String>>> opError = t -> Flux.just(Try.failure(new Exception("should fail")));
    tryFlux = tryFlux.flatMap(ReactorVavrUtils.tryToFluxTry(opError));
    StepVerifier.create(tryFlux).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void shorterTryToMonoTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Mono<Try<Integer>>> op1 = t -> Mono.just(Try.success((t + " world").length()));
    Function<Integer, Mono<Try<String>>> op2 = t -> Mono.just(Try.success("count is " + t));
    Mono<Try<String>> tryMono = Mono.just(h)
                                    .flatMap(ReactorVavrUtils.tryToMonoTry(op1))
                                    .flatMap(ReactorVavrUtils.tryToMonoTry(op2));
    StepVerifier.create(tryMono).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Mono<Try<String>>> opError = t -> Mono.just(Try.failure(new Exception("should fail")));
    tryMono = tryMono.flatMap(ReactorVavrUtils.tryToMonoTry(opError));
    StepVerifier.create(tryMono).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void tryToFluxTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Flux<Try<Integer>>> op1 = t -> Flux.just(Try.success((t + " world").length()));
    Function<Integer, Flux<Try<String>>> op2 = t -> Flux.just(Try.success("count is " + t));
    Flux<Try<String>> tryFlux = Flux.just(h)
                                    .flatMap(k -> ReactorVavrUtils.tryToFluxTry(k, op1))
                                    .flatMap(r -> ReactorVavrUtils.tryToFluxTry(r, op2));
    StepVerifier.create(tryFlux).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Flux<Try<String>>> opError = t -> Flux.just(Try.failure(new Exception("should fail")));
    tryFlux = tryFlux.flatMap(k -> ReactorVavrUtils.tryToFluxTry(k, opError));
    StepVerifier.create(tryFlux).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void tryToMonoTest() {
    var tSuccess = Try.success("hello");
    StepVerifier.create(tryToMono(tSuccess)).expectNext("hello").expectComplete().verify();
    var tFailure = Try.failure(new Exception("Exception Happened"));
    StepVerifier.create(tryToMono(tFailure)).expectError(Exception.class).verify();
  }

  @Test
  public void tryToMonoTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Mono<Try<Integer>>> op1 = t -> Mono.just(Try.success((t + " world").length()));
    Function<Integer, Mono<Try<String>>> op2 = t -> Mono.just(Try.success("count is " + t));
    Mono<Try<String>> tryMono = Mono.just(h)
                                    .flatMap(k -> ReactorVavrUtils.tryToMonoTry(k, op1))
                                    .flatMap(r -> ReactorVavrUtils.tryToMonoTry(r, op2));
    StepVerifier.create(tryMono).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Mono<Try<String>>> opError = t -> Mono.just(Try.failure(new Exception("should fail")));
    tryMono = tryMono.flatMap(k -> ReactorVavrUtils.tryToMonoTry(k, opError));
    StepVerifier.create(tryMono).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  @DisplayName("tryToMonoVoid test")
  public void tryToMonoVoidsTest() {
    Mono<Try<String>> original = Mono.just(Try.success("one"));
    Function<String, Mono<Void>> deferredOp = (String content) -> FileUtils.writeFile("target/one.txt",
                                                                                      content,
                                                                                      Schedulers.elastic()).then();
    Function<Throwable, Mono<Void>> throwable = t -> Mono.just(Try.failure(new Exception("should not fail"))).then();
    Mono<Void> voidMono = original.flatMap(ReactorVavrUtils.tryToMonoVoid(deferredOp, throwable));
    StepVerifier.create(voidMono).expectComplete().verify();
  }
}
