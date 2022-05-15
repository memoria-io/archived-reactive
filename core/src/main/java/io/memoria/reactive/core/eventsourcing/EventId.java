package io.memoria.reactive.core.eventsourcing;

import io.memoria.reactive.core.id.Id;

import java.util.UUID;

public record EventId(String value) implements Id {
  public EventId {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
  }

  public static EventId randomUUID() {
    return of(UUID.randomUUID());
  }

  public static EventId of(UUID id) {
    return new EventId(id.toString());
  }

  public static EventId of(long id) {
    return new EventId(Long.toString(id));
  }

  public static EventId of(String id) {
    return new EventId(id);
  }
}
