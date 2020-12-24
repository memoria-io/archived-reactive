package io.memoria.jutils.core.eventsourcing;

import io.memoria.jutils.core.value.Id;

import java.util.Objects;

public abstract class Entity<S> {
  public final Id id;
  public final S value;

  public Entity(Id id, S value) {
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
    return id.equals(entity.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
