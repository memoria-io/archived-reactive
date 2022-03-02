package io.memoria.reactive.core.id;

import java.io.Serializable;
import java.util.UUID;

public record Id(String value) implements Serializable {
  public Id(long id) {
    this(Long.toString(id));
  }

  public Id {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
  }

  public static Id of(UUID id) {
    return new Id(id.toString());
  }

  public static Id of(long id) {
    return new Id(id);
  }

  public static Id of(String id) {
    return new Id(id);
  }
}
