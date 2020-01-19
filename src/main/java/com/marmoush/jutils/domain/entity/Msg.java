package com.marmoush.jutils.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Msg extends Entity<String> {
  public final LocalDateTime creationTime;

  public Msg(String id, String value) {
    this(id, value, LocalDateTime.now());
  }

  public Msg(String id, String value, LocalDateTime creationTime) {
    super(id, value);
    this.creationTime = creationTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    Msg msg = (Msg) o;
    return creationTime.equals(msg.creationTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), creationTime);
  }
}
