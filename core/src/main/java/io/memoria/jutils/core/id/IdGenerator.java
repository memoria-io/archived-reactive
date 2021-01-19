package io.memoria.jutils.core.id;

import io.memoria.jutils.core.id.Id;

import java.util.function.Supplier;

@FunctionalInterface
public interface IdGenerator extends Supplier<Id> {
  @Override
  Id get();
}
