package io.memoria.jutils.core.dto;

import io.vavr.collection.List;
import io.vavr.control.Try;

import java.util.Objects;
import java.util.function.Supplier;

import static java.util.function.Function.identity;

@FunctionalInterface
public interface DTO<T> extends Supplier<Try<T>> {
  static <T> Try<T> firstNonNull(List<DTO<? extends T>> dtoList, Throwable throwable) {
    var obj = dtoList.find(Objects::nonNull);
    return (obj.isDefined()) ? obj.get().get().map(identity()) : Try.failure(throwable);
  }
}
