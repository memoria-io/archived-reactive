package io.memoria.jutils.core.dto;

import io.vavr.control.Try;

import java.util.function.Supplier;

import static io.memoria.jutils.core.dto.DTOException.NULL_PROPERTIES;
import static java.util.function.Function.identity;

@FunctionalInterface
public interface DTO<T> extends Supplier<Try<T>> {
  @SafeVarargs
  static <T> Try<T> getNonNull(DTO<? extends T>... ts) {
    for (DTO<? extends T> t : ts) {
      if (t != null)
        return t.get().map(identity());
    }
    return Try.failure(NULL_PROPERTIES);
  }

  @SafeVarargs
  static <T> Try<T> getNonNull(T... ts) {
    for (T t : ts) {
      if (t != null)
        return Try.success(t);
    }
    return Try.failure(NULL_PROPERTIES);
  }
}
