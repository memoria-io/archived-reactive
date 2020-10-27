package io.memoria.jutils.core.generator;

import io.memoria.jutils.core.value.Id;

import java.util.function.Supplier;

@FunctionalInterface
public interface IdGenerator extends Supplier<Id> {
  @Override
  Id get();
}
