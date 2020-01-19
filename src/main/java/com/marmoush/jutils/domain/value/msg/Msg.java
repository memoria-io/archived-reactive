package com.marmoush.jutils.domain.value.msg;

import java.time.LocalDateTime;
import java.util.Objects;

public class Msg {
  public final String value;
  public final LocalDateTime creationTime;

  public Msg(String value) {
    this.value = value;
    this.creationTime = LocalDateTime.now();
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
    return Objects.equals(value, msg.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
