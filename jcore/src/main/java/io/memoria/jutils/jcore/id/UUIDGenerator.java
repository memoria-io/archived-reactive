package io.memoria.jutils.jcore.id;

import java.util.UUID;

public record UUIDGenerator() implements IdGenerator {
  @Override
  public Id get() {
    return Id.of(UUID.randomUUID().toString());
  }
}
