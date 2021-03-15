package io.memoria.jutils.jcore.vavr;

import io.vavr.API;
import io.vavr.Patterns;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;
import java.util.function.Function;

import static java.lang.Boolean.TRUE;

public final class ReactorVavrUtils {
  public static <T> Flux<T> toFlux(Try<List<T>> t) {
    return t.isSuccess() ? Flux.fromIterable(t.get()) : Flux.error(t.getCause());
  }

  public static <L extends Throwable, R> Mono<R> toMono(Either<L, R> either) {
    return either.isRight() ? Mono.just(either.get()) : Mono.error(either.getLeft());
  }

  public static <T> Mono<T> toMono(Try<T> t) {
    return t.isSuccess() ? Mono.just(t.get()) : Mono.error(t.getCause());
  }

  public static <T> Mono<T> toMono(Option<T> option) {
    return (option.isDefined()) ? Mono.just(option.get()) : Mono.empty();
  }

  public static <T> Mono<T> toMono(Option<T> option, Throwable throwable) {
    return (option.isDefined()) ? Mono.just(option.get()) : Mono.error(throwable);
  }

  public static <T> Function<Boolean, Mono<T>> toMono(Callable<T> t, Throwable throwable) {
    return b -> TRUE.equals(b) ? Mono.fromCallable(t) : Mono.error(throwable);
  }

  public static <A, B> Flux<Try<B>> toTryFlux(Try<A> a, Function<A, Flux<Try<B>>> f) {
    return API.Match(a)
              .of(API.Case(Patterns.$Success(API.$()), f),
                  API.Case(Patterns.$Failure(API.$()), t -> Flux.just(Try.failure(t))));
  }

  public static <A, B> Function<Try<A>, Flux<Try<B>>> toTryFlux(Function<A, Flux<Try<B>>> f) {
    return a -> API.Match(a)
                   .of(API.Case(Patterns.$Success(API.$()), f),
                       API.Case(Patterns.$Failure(API.$()), t -> Flux.just(Try.<B>failure(t))));
  }

  public static <A, B> Mono<Try<B>> toTryMono(Try<A> a, Function<A, Mono<Try<B>>> f) {
    return API.Match(a)
              .of(API.Case(Patterns.$Success(API.$()), f),
                  API.Case(Patterns.$Failure(API.$()), t -> Mono.just(Try.failure(t))));
  }

  public static <A, B> Function<Try<A>, Mono<Try<B>>> toTryMono(Function<A, Mono<Try<B>>> f) {
    return a -> API.Match(a)
                   .of(API.Case(Patterns.$Success(API.$()), f),
                       API.Case(Patterns.$Failure(API.$()), t -> Mono.just(Try.<B>failure(t))));
  }

  public static Function<Boolean, Mono<Void>> toVoidMono(Runnable t, Throwable throwable) {
    return b -> TRUE.equals(b) ? Mono.fromRunnable(t) : Mono.error(throwable);
  }

  public static <A> Function<Try<A>, Mono<Void>> toVoidMono(Function<A, Mono<Void>> f,
                                                            Function<Throwable, Mono<Void>> f2) {
    return a -> API.Match(a).of(API.Case(Patterns.$Success(API.$()), f), API.Case(Patterns.$Failure(API.$()), f2));
  }

  private ReactorVavrUtils() {}
}
