package io.memoria.reactive.core.vavr;

import io.vavr.API;
import io.vavr.Patterns;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static java.lang.Boolean.TRUE;
import static java.util.function.Function.identity;

public final class ReactorVavrUtils {
  private ReactorVavrUtils() {}

  public static void closeReader(Closeable closeable) {
    try {
      closeable.close();
    } catch (IOException e) {
      throw Exceptions.propagate(e);
    }
  }

  public static <T> Flux<T> toFlux(Try<List<T>> tr) {
    return Mono.fromCallable(() -> tr.isSuccess() ? Flux.fromIterable(tr.get()) : Flux.<T>error(tr.getCause()))
               .flatMapMany(identity());
  }

  public static <L extends Throwable, R> Mono<R> toMono(Either<L, R> either) {
    return Mono.fromCallable(() -> either.isRight() ? Mono.just(either.get()) : Mono.<R>error(either.getLeft()))
               .flatMap(identity());
  }

  public static <T> Mono<T> toMono(Try<T> t) {
    return Mono.fromCallable(() -> t.isSuccess() ? Mono.just(t.get()) : Mono.<T>error(t.getCause()))
               .flatMap(identity());
  }

  public static <T> Mono<T> toMono(Option<T> option) {
    return Mono.fromCallable(() -> (option.isDefined()) ? Mono.just(option.get()) : Mono.<T>empty())
               .flatMap(identity());
  }

  public static <T> Mono<T> toMono(Option<T> option, Throwable throwable) {
    return Mono.fromCallable(() -> (option.isDefined()) ? Mono.just(option.get()) : Mono.<T>error(throwable))
               .flatMap(identity());
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
}
