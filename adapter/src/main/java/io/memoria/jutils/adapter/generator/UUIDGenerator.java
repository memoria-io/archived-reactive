package io.memoria.jutils.adapter.generator;

import io.memoria.jutils.core.generator.IdGenerator;

import java.util.UUID;

public record UUIDGenerator() implements IdGenerator {
  @Override
  public String get() {
    return UUID.randomUUID().toString();
  }
}
