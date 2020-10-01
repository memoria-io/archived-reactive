package io.memoria.jutils.core.dto;

import io.vavr.control.Try;

import java.util.Arrays;
import java.util.function.Supplier;

@FunctionalInterface
public interface DTO<T> extends Supplier<Try<T>> {
  static NullPointerException nullProperties(String... properties) {
    return new NullPointerException("Non of the properties " + Arrays.asList(properties) + " was initialized");
  }
}
