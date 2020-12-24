package io.memoria.jutils.core.value;

public record Id(String value) {
  public static Id of(long id) {
    return new Id(id + "");
  }

  public Id {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
  }
}
