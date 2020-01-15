package com.marmoush.jutils.domain.value.msg;

import io.vavr.control.Option;

import java.time.LocalDateTime;
import java.util.Objects;

import static io.vavr.control.Option.none;

public class ConsumerResp<T> {

  public final Msg msg;
  public final LocalDateTime consumingTime;
  public final Option<T> t;

  public ConsumerResp(Msg msg) {
    this(msg, LocalDateTime.now());
  }

  public ConsumerResp(Msg msg, LocalDateTime consumingTime) {
    this(msg, consumingTime, none());
  }

  public ConsumerResp(Msg msg, LocalDateTime consumingTime, Option<T> t) {
    this.msg = msg;
    this.consumingTime = consumingTime;
    this.t = t;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ConsumerResp<?> consumerResp = (ConsumerResp<?>) o;
    return Objects.equals(msg, consumerResp.msg) && Objects.equals(consumingTime, consumerResp.consumingTime) &&
           Objects.equals(t, consumerResp.t);
  }

  @Override
  public int hashCode() {
    return Objects.hash(msg, consumingTime, t);
  }
}
