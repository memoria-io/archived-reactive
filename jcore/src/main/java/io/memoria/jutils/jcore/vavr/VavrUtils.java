package io.memoria.jutils.jcore.vavr;

import io.vavr.API;
import io.vavr.Predicates;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import io.vavr.control.Try;

import java.util.function.BiFunction;

public final class VavrUtils {
  public static <V> BiFunction<V, Throwable, Try<V>> handle() {
    return (v, t) -> (t == null) ? Try.success(v) : Try.failure(t);
  }

  public static <V> BiFunction<V, Throwable, Try<Void>> handleToVoid() {
    return (v, t) -> (t == null) ? Try.success(null) : Try.failure(t);
  }

  public static <T, R> API.Match.Case<T, R> instanceOfCase(Class<?> c, R r) {
    return API.Case(API.$(Predicates.instanceOf(c)), () -> r);
  }

  public static <T> List<Try<T>> listOfTry(Try<List<T>> tt) {
    if (tt.isSuccess())
      return tt.get().map(Try::success);
    else
      return List.of(Try.failure(tt.getCause()));
  }

  public static <A extends Traversable<B>, B> Traversable<Try<B>> traverseOfTry(Try<A> tt) {
    if (tt.isSuccess())
      return tt.get().map(Try::success);
    else
      return List.of(Try.failure(tt.getCause()));
  }

  private VavrUtils() {}
}
