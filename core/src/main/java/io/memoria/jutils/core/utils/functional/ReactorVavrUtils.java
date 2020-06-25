package io.memoria.jutils.core.utils.functional;

import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;
import io.vavr.control.Either;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;

public final class ReactorVavrUtils {
  public static <A> Mono<A> blockingToMono(CheckedFunction0<A> supplier, Scheduler scheduler) {
    return Mono.defer(() -> checkedMono(supplier).subscribeOn(scheduler));
  }

  public static Mono<Void> blockingToVoidMono(CheckedRunnable runnable, Scheduler scheduler) {
    return Mono.defer(() -> checkedMono(runnable).subscribeOn(scheduler));
  }

  public static <T> Mono<T> checkedMono(CheckedFunction0<? extends T> supplier) {
    Objects.requireNonNull(supplier, "Supplier is null");
    try {
      return Mono.just(supplier.apply());
    } catch (Throwable t) {
      return Mono.error(t);
    }
  }

  public static Mono<Void> checkedMono(CheckedRunnable runnable) {
    Objects.requireNonNull(runnable, "Runnable is null");
    return Mono.create(s -> {
      try {
        runnable.run();
        s.success();
      } catch (Throwable throwable) {
        s.error(throwable);
      }
    });
  }

  public static <L extends Throwable, R> Mono<R> eitherToMono(Either<L, R> either) {
    if (either.isRight())
      return Mono.just(either.get());
    else
      return Mono.error(either.getLeft());
  }

  public static <T> Mono<T> futureToMono(Future<T> f, Duration d, Scheduler scheduler) {
    return blockingToMono(() -> f.get(d.toMillis(), TimeUnit.MILLISECONDS), scheduler);
  }

  public static <A, B> Flux<Try<B>> tryToFluxTry(Try<A> a, Function<A, Flux<Try<B>>> f) {
    return Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Flux.just(Try.failure(t))));
  }

  public static <A, B> Function<Try<A>, Flux<Try<B>>> tryToFluxTry(Function<A, Flux<Try<B>>> f) {
    return a -> Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Flux.just(Try.<B>failure(t))));
  }

  public static <A, B> Mono<Try<B>> tryToMonoTry(Try<A> a, Function<A, Mono<Try<B>>> f) {
    return Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Mono.just(Try.failure(t))));
  }

  public static <A, B> Function<Try<A>, Mono<Try<B>>> tryToMonoTry(Function<A, Mono<Try<B>>> f) {
    return a -> Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Mono.just(Try.<B>failure(t))));
  }

  public static <A> Function<Try<A>, Mono<Void>> tryToMonoVoid(Function<A, Mono<Void>> f,
                                                               Function<Throwable, Mono<Void>> f2) {
    return a -> Match(a).of(Case($Success($()), f), Case($Failure($()), f2));
  }

  private ReactorVavrUtils() {}
}
