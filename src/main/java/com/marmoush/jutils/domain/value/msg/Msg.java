package com.marmoush.jutils.domain.value.msg;

import io.vavr.control.Option;

import java.util.Objects;

public class Msg {
  public final String value;

  /**
   * Partitioning Key
   */
  public final Option<String> pkey;

  public Msg(String value, Option<String> pkey) {
    this.value = value;
    this.pkey = pkey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Msg msg = (Msg) o;
    return Objects.equals(pkey, msg.pkey) && Objects.equals(value, msg.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pkey, value);
  }
}
