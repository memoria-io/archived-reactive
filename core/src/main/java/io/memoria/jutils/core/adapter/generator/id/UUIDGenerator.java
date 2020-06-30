package io.memoria.jutils.core.adapter.generator.id;

import io.memoria.jutils.core.domain.port.IdGenerator;

import java.util.UUID;

public record UUIDGenerator() implements IdGenerator {
  @Override
  public String get() {
    return UUID.randomUUID().toString();
  }
}
