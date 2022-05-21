package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

import java.util.UUID;

public record CommandId(String value) implements Id {
  public CommandId {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
  }

  public static CommandId randomUUID() {
    return of(UUID.randomUUID());
  }

  public static CommandId of(UUID id) {
    return new CommandId(id.toString());
  }

  public static CommandId of(long id) {
    return new CommandId(Long.toString(id));
  }

  public static CommandId of(String id) {
    return new CommandId(id);
  }
}