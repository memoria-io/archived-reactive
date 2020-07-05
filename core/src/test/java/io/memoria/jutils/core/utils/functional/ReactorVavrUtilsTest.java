package io.memoria.jutils.core.utils.functional;

import io.memoria.jutils.core.utils.file.FileUtils;
import io.vavr.CheckedRunnable;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toMono;
import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toVoidMono;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

public class ReactorVavrUtilsTest {
  @Test
  public void blockingToMono() {
    class ThreadName {
      public String threadName;
    }
    final ThreadName n = new ThreadName();
    var m = toMono(() -> n.threadName = Thread.currentThread().getName(), Schedulers.newElastic("MyElasticThread"));
    Assertions.assertNull(n.threadName);
    m.block();
    Assertions.assertNotNull(n.threadName);
    Assertions.assertTrue(n.threadName.contains("MyElasticThread"));
  }

  @Test
  public void eitherToMonoTest() {
    Either<Exception, Integer> k = right(23);
    Mono<Integer> integerMono = ReactorVavrUtils.toMono(k);
    StepVerifier.create(integerMono).expectNext(23).expectComplete().verify();

    k = left(new Exception("exception example"));
    integerMono = ReactorVavrUtils.toMono(k);
    StepVerifier.create(integerMono).expectError().verify();
  }

  @Test
  public void futureToMono() {
    var output = "Data is processed";
    class DataProcessor implements Callable<String> {
      @Override
      public String call() throws Exception {
        TimeUnit.MILLISECONDS.sleep(100);
        return output;
      }
    }
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    var dataReadFuture = executorService.submit(new DataProcessor());
    var mSuccess = ReactorVavrUtils.toMono(dataReadFuture,
                                           Duration.ofMillis(200),
                                           Schedulers.fromExecutor(executorService));
    StepVerifier.create(mSuccess).expectNext(output).expectComplete().verify();

    var dataReadFutureFail = executorService.submit(new DataProcessor());
    var mFail = ReactorVavrUtils.toMono(dataReadFutureFail,
                                        Duration.ofMillis(1),
                                        Schedulers.fromExecutor(executorService));
    StepVerifier.create(mFail).expectError(TimeoutException.class).verify();
  }

  @Test
  public void shorterTryToFluxTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Flux<Try<Integer>>> op1 = t -> Flux.just(Try.success((t + " world").length()));
    Function<Integer, Flux<Try<String>>> op2 = t -> Flux.just(Try.success("count is " + t));
    Flux<Try<String>> tryFlux = Flux.just(h)
                                    .flatMap(ReactorVavrUtils.toTryFlux(op1))
                                    .flatMap(ReactorVavrUtils.toTryFlux(op2));
    StepVerifier.create(tryFlux).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Flux<Try<String>>> opError = t -> Flux.just(Try.failure(new Exception("should fail")));
    tryFlux = tryFlux.flatMap(ReactorVavrUtils.toTryFlux(opError));
    StepVerifier.create(tryFlux).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void shorterTryToMonoTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Mono<Try<Integer>>> op1 = t -> Mono.just(Try.success((t + " world").length()));
    Function<Integer, Mono<Try<String>>> op2 = t -> Mono.just(Try.success("count is " + t));
    Mono<Try<String>> tryMono = Mono.just(h)
                                    .flatMap(ReactorVavrUtils.toTryMono(op1))
                                    .flatMap(ReactorVavrUtils.toTryMono(op2));
    StepVerifier.create(tryMono).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Mono<Try<String>>> opError = t -> Mono.just(Try.failure(new Exception("should fail")));
    tryMono = tryMono.flatMap(ReactorVavrUtils.toTryMono(opError));
    StepVerifier.create(tryMono).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void tryToFluxTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Flux<Try<Integer>>> op1 = t -> Flux.just(Try.success((t + " world").length()));
    Function<Integer, Flux<Try<String>>> op2 = t -> Flux.just(Try.success("count is " + t));
    Flux<Try<String>> tryFlux = Flux.just(h)
                                    .flatMap(k -> ReactorVavrUtils.toTryFlux(k, op1))
                                    .flatMap(r -> ReactorVavrUtils.toTryFlux(r, op2));
    StepVerifier.create(tryFlux).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Flux<Try<String>>> opError = t -> Flux.just(Try.failure(new Exception("should fail")));
    tryFlux = tryFlux.flatMap(k -> ReactorVavrUtils.toTryFlux(k, opError));
    StepVerifier.create(tryFlux).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void tryToMonoTest() {
    var tSuccess = Try.success("hello");
    StepVerifier.create(ReactorVavrUtils.toMono(tSuccess)).expectNext("hello").expectComplete().verify();
    var tFailure = Try.failure(new Exception("Exception Happened"));
    StepVerifier.create(ReactorVavrUtils.toMono(tFailure)).expectError(Exception.class).verify();
  }

  @Test
  public void tryToMonoTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Mono<Try<Integer>>> op1 = t -> Mono.just(Try.success((t + " world").length()));
    Function<Integer, Mono<Try<String>>> op2 = t -> Mono.just(Try.success("count is " + t));
    Mono<Try<String>> tryMono = Mono.just(h)
                                    .flatMap(k -> ReactorVavrUtils.toTryMono(k, op1))
                                    .flatMap(r -> ReactorVavrUtils.toTryMono(r, op2));
    StepVerifier.create(tryMono).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Mono<Try<String>>> opError = t -> Mono.just(Try.failure(new Exception("should fail")));
    tryMono = tryMono.flatMap(k -> ReactorVavrUtils.toTryMono(k, opError));
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
    Mono<Void> voidMono = original.flatMap(ReactorVavrUtils.toVoidMono(deferredOp, throwable));
    StepVerifier.create(voidMono).expectComplete().verify();
  }

  @Test
  public void toVoidMonoTest() {
    AtomicBoolean b = new AtomicBoolean();
    CheckedRunnable r = () -> b.set(true);
    var v = toVoidMono(r, Schedulers.elastic());
    StepVerifier.create(v).expectComplete().verify();
    Assertions.assertTrue(b.get());
  }

  @Test
  public void toVoidMonoTestFail() {
    var v = toVoidMono(this::hi, Schedulers.elastic());
    StepVerifier.create(v).expectError(IOException.class).verify();
  }

  private void hi() throws IOException {
    throw new IOException();
  }
}
