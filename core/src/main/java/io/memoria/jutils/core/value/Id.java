package io.memoria.jutils.core.value;

public record Id(String value) {
  public Id {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
  }
}
