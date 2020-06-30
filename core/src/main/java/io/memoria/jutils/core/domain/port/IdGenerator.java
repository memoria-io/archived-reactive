package io.memoria.jutils.core.domain.port;

import java.util.function.Supplier;

@FunctionalInterface
public interface IdGenerator extends Supplier<String> {
  @Override
  String get();
}
