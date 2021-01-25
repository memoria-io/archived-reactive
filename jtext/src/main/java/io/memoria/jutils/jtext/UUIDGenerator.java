package io.memoria.jutils.jtext;

import io.memoria.jutils.core.id.Id;

import java.util.UUID;

public record UUIDGenerator() implements IdGenerator {
  @Override
  public Id get() {
    return Id.of(UUID.randomUUID().toString());
  }
}
