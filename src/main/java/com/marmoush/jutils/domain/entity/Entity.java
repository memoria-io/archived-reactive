package com.marmoush.jutils.domain.entity;

import java.util.Objects;

public class Entity<T> {
  public final String id;
  public final T value;

  public Entity(String id, T value) {
    this.id = id;
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Entity<?> entity = (Entity<?>) o;
    return id.equals(entity.id) && value.equals(entity.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, value);
  }
}
