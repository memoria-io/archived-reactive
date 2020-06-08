package io.memoria.jutils.core.domain.entity;

import io.vavr.control.Option;

import java.time.LocalDateTime;
import java.util.Objects;

import static io.vavr.control.Option.none;

public class Entity<T> {
  public final String id;
  public final T value;
  public final LocalDateTime creationTime;
  public final Option<String> flowId;

  public Entity(String id, T value) {
    this(id, value, LocalDateTime.now(), none());
  }

  public Entity(String id, T value, LocalDateTime creationTime, Option<String> flowId) {
    this.id = id;
    this.value = value;
    this.creationTime = creationTime;
    this.flowId = flowId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Entity<?> entity = (Entity<?>) o;
    return id.equals(entity.id) && value.equals(entity.value) && creationTime.equals(entity.creationTime) &&
           flowId.equals(entity.flowId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, value, creationTime, flowId);
  }
}
