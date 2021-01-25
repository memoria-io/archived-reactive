package io.memoria.jutils.jtext;

import io.memoria.jutils.core.id.Id;

import java.util.function.Supplier;

@FunctionalInterface
public interface IdGenerator extends Supplier<Id> {
  @Override
  Id get();
}
