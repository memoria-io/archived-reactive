package io.memoria.jutils.jcore.id;

import java.util.Objects;

public class Id {
  public static Id of(long id) {
    return new Id(id + "");
  }

  public static Id of(String id) {
    return new Id(id);
  }

  private final String value;

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

  @Override
  public String toString() {
    return value;
  }

  public String value() {
    return value;
  }
}
