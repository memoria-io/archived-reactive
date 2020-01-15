package com.marmoush.jutils.utils.functional;

import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import io.vavr.control.Try;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static java.util.Objects.requireNonNullElseGet;

public final class VavrUtils {
  private VavrUtils() {}

  public static <T, R> API.Match.Case<T, R> instanceOfCase(Class<?> c, R r) {
    return Case($(instanceOf(c)), () -> r);
  }

  public static <A extends Traversable<B>, B> Traversable<Try<B>> traversableT(Try<A> tt) {
    if (tt.isSuccess())
      return tt.get().map(Try::success);
    else
      return List.of(Try.failure(tt.getCause()));
  }

  public static <V> BiFunction<V, Throwable, Try<V>> handle() {
    return (v, t) -> (v != null) ? Try.success(v) : Try.failure(t);
  }
}
