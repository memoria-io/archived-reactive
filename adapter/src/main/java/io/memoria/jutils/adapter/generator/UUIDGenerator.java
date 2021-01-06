package io.memoria.jutils.adapter.generator;

import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.core.value.Id;

import java.util.UUID;

public record UUIDGenerator() implements IdGenerator {
  @Override
  public Id get() {
    return Id.of(UUID.randomUUID().toString());
  }
}
