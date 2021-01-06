package io.memoria.jutils.core.value;

import java.util.Objects;

public class Id {
  private final String value;

  public static Id of(long id) {
    return new Id(id + "");
  }

  public static Id of(String id) {
    return new Id(id);
  }

  private Id(String value) {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException("Id value is null or empty.");
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Id id = (Id) o;
    return value.equals(id.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  public String value() {
    return value;
  }
}
