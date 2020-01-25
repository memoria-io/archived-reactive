package com.marmoush.jutils.utils.functional;

import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import io.vavr.control.Try;

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

public final class VavrUtils {
  private VavrUtils() {}

  public static <T, R> API.Match.Case<T, R> instanceOfCase(Class<?> c, R r) {
    return Case($(instanceOf(c)), () -> r);
  }

  public static <A extends Traversable<B>, B> Traversable<Try<B>> traverseOfTry(Try<A> tt) {
    if (tt.isSuccess())
      return tt.get().map(Try::success);
    else
      return List.of(Try.failure(tt.getCause()));
  }

  public static <T> List<Try<T>> listOfTry(Try<List<T>> tt) {
    if (tt.isSuccess())
      return tt.get().map(Try::success);
    else
      return List.of(Try.failure(tt.getCause()));
  }

  public static <V> BiFunction<V, Throwable, Try<V>> handle() {
    return (v, t) -> (t == null) ? Try.success(v) : Try.failure(t);
  }

  public static <V> BiFunction<V, Throwable, Try<Void>> handleToVoid() {
    return (v, t) -> (t == null) ? Try.success(null) : Try.failure(t);
  }

  public static <A, B> Function<Try<A>, Try<B>> tryMap(Function<A, B> f) {
    return a -> a.map(g -> f.apply(g));
  }

  public static <A, B> Function<Try<A>, Try<B>> tryVoid() {
    return a -> a.map(g -> null);
  }
}
