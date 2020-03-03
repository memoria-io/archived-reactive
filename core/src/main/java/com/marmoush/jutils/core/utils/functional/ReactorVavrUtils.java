package com.marmoush.jutils.core.utils.functional;

import io.vavr.control.Either;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.function.Function;
import java.util.function.Supplier;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;

public class ReactorVavrUtils {
  private ReactorVavrUtils() {}

  public static <A, B> Mono<Try<B>> tryToMonoTry(Try<A> a, Function<A, Mono<Try<B>>> f) {
    return Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Mono.just(Try.failure(t))));
  }

  public static <A, B> Flux<Try<B>> tryToFluxTry(Try<A> a, Function<A, Flux<Try<B>>> f) {
    return Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Flux.just(Try.failure(t))));
  }

  public static <A, B> Function<Try<A>, Mono<Try<B>>> tryToMonoTry(Function<A, Mono<Try<B>>> f) {
    return a -> Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Mono.just(Try.<B>failure(t))));
  }

  public static <A, B> Function<Try<A>, Flux<Try<B>>> tryToFluxTry(Function<A, Flux<Try<B>>> f) {
    return a -> Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Flux.just(Try.<B>failure(t))));
  }

  public static <A> Function<Try<A>, Mono<Void>> tryToMonoVoid(Function<A, Mono<Void>> f,
                                                               Function<Throwable, Mono<Void>> f2) {
    return a -> Match(a).of(Case($Success($()), f), Case($Failure($()), f2));
  }

  public static <A> Mono<A> blockingToMono(Supplier<A> f, Scheduler scheduler) {
    return Mono.defer(() -> Mono.just(f.get()).subscribeOn(scheduler));
  }

  public static <L extends Throwable, R> Mono<R> eitherToMono(Either<L, R> either) {
    if (either.isRight())
      return Mono.just(either.get());
    else
      return Mono.error(either.getLeft());
  }
}
