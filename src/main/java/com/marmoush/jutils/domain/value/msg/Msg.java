package com.marmoush.jutils.domain.value.msg;

import java.time.LocalDateTime;
import java.util.Objects;

public class Msg {
  public final String value;
  public final LocalDateTime creationTime;

  public Msg(String value) {
    this(value, LocalDateTime.now());
  }

  public Msg(String value, LocalDateTime creationTime) {
    this.value = value;
    this.creationTime = creationTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Msg msg = (Msg) o;
    return value.equals(msg.value) && creationTime.equals(msg.creationTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, creationTime);
  }
}
