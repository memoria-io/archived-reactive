package io.memoria.reactive.core.id;

import java.io.Serializable;

public record Id(String id) implements Serializable {
  public Id(long id) {
    this(id + "");
  }

  public Id {
    if (id == null || id.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
  }

  public static Id of(long id) {
    return new Id(id + "");
  }

  public static Id of(String id) {
    return new Id(id);
  }
}
