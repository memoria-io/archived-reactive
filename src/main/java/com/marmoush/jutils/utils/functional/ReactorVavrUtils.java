package com.marmoush.jutils.utils.functional;

import io.vavr.*;
import io.vavr.control.Try;
import io.vavr.control.*;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;

import java.util.function.Function;
import java.util.function.*;

import static io.vavr.API.*;
import static io.vavr.Patterns.*;

public class ReactorVavrUtils {
  private ReactorVavrUtils() {}

  public static <A, B> Mono<Try<B>> tryToMonoTry(Try<A> a, Function<A, Mono<Try<B>>> f) {
    return Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Mono.just(Try.<B>failure(t))));
  }

  public static <A, B> Flux<Try<B>> tryToFluxTry(Try<A> a, Function<A, Flux<Try<B>>> f) {
    return Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Flux.just(Try.<B>failure(t))));
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

  public interface MFn1<T, R> extends Function1<T, Mono<Try<R>>> {}

  public interface FFn1<T, R> extends Function1<T, Flux<Try<R>>> {}

  public interface MFn2<T1, T2, R> extends Function2<T1, T2, Mono<Try<R>>> {}

  public interface FFn2<T1, T2, R> extends Function2<T1, T2, Flux<Try<R>>> {}

  public interface MFn3<T1, T2, T3, R> extends Function3<T1, T2, T3, Mono<Try<R>>> {}

  public interface FFn3<T1, T2, T3, R> extends Function3<T1, T2, T3, Flux<Try<R>>> {}

  public interface MFn4<T1, T2, T3, T4, R> extends Function4<T1, T2, T3, T4, Mono<Try<R>>> {}

  public interface FFn4<T1, T2, T3, T4, R> extends Function4<T1, T2, T3, T4, Flux<Try<R>>> {}

}
