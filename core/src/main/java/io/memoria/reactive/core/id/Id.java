package io.memoria.reactive.core.id;

import java.io.Serializable;

public record Id(String value) implements Serializable {
  public Id(long id) {
    this(id + "");
  }

  public Id {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
  }

  public static Id of(long id) {
    return new Id(id + "");
  }

  public static Id of(String id) {
    return new Id(id);
  }
}
