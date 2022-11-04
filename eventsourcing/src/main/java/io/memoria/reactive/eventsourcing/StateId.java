package io.memoria.reactive.eventsourcing;

import io.memoria.atom.core.id.Id;

import java.util.UUID;

public record StateId(String value) implements Id {
  public StateId {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
  }

  public static StateId of(Id id) {
    return new StateId(id.value());
  }

  public static StateId randomUUID() {
    return of(UUID.randomUUID());
  }

  public static StateId of(UUID id) {
    return new StateId(id.toString());
  }

  public static StateId of(long id) {
    return new StateId(Long.toString(id));
  }

  public static StateId of(String id) {
    return new StateId(id);
  }
}
