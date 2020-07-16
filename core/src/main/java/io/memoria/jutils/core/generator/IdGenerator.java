package io.memoria.jutils.core.generator;

import java.util.function.Supplier;

@FunctionalInterface
public interface IdGenerator extends Supplier<String> {
  @Override
  String get();
}
