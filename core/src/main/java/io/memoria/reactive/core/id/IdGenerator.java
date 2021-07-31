package io.memoria.reactive.core.id;

import java.util.function.Supplier;

@FunctionalInterface
public interface IdGenerator extends Supplier<Id> {
  @Override
  Id get();
}
