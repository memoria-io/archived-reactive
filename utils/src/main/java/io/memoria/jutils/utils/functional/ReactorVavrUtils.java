package io.memoria.jutils.utils.functional;

import io.vavr.control.Either;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;

public final class ReactorVavrUtils {
  public static <L extends Throwable, R> Mono<R> toMono(Either<L, R> either) {
    if (either.isRight())
      return Mono.just(either.get());
    else
      return Mono.error(either.getLeft());
  }

  public static <T> Mono<T> toMono(Try<T> t) {
    if (t.isSuccess()) {
      return Mono.just(t.get());
    } else {
      return Mono.error(t.getCause());
    }
  }

  public static <A, B> Flux<Try<B>> toTryFlux(Try<A> a, Function<A, Flux<Try<B>>> f) {
    return Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Flux.just(Try.failure(t))));
  }

  public static <A, B> Function<Try<A>, Flux<Try<B>>> toTryFlux(Function<A, Flux<Try<B>>> f) {
    return a -> Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Flux.just(Try.<B>failure(t))));
  }

  public static <A, B> Mono<Try<B>> toTryMono(Try<A> a, Function<A, Mono<Try<B>>> f) {
    return Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Mono.just(Try.failure(t))));
  }

  public static <A, B> Function<Try<A>, Mono<Try<B>>> toTryMono(Function<A, Mono<Try<B>>> f) {
    return a -> Match(a).of(Case($Success($()), f), Case($Failure($()), t -> Mono.just(Try.<B>failure(t))));
  }

  public static <A> Function<Try<A>, Mono<Void>> toVoidMono(Function<A, Mono<Void>> f,
                                                            Function<Throwable, Mono<Void>> f2) {
    return a -> Match(a).of(Case($Success($()), f), Case($Failure($()), f2));
  }

  private ReactorVavrUtils() {}
}
