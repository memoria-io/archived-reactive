package io.memoria.jutils.adapter.id;

import io.memoria.jutils.core.id.Id;
import io.memoria.jutils.core.id.IdGenerator;

import java.util.UUID;

public record UUIDGenerator() implements IdGenerator {
  @Override
  public Id get() {
    return Id.of(UUID.randomUUID().toString());
  }
}
