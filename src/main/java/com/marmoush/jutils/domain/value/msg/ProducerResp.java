package com.marmoush.jutils.domain.value.msg;

import io.vavr.control.Option;

import java.time.LocalDateTime;
import java.util.Objects;

import static io.vavr.control.Option.none;

public class ProducerResp<T> {
  public final Option<T> t;
  public final LocalDateTime publishTime;

  public ProducerResp() {
    this(none());
  }

  public ProducerResp(Option<T> t) {
    this.t = t;
    this.publishTime = LocalDateTime.now();
  }

  public ProducerResp(Option<T> t, LocalDateTime publishTime) {
    this.t = t;
    this.publishTime = publishTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ProducerResp<?> producerResp = (ProducerResp<?>) o;
    return Objects.equals(t, producerResp.t);
  }

  @Override
  public int hashCode() {
    return Objects.hash(t);
  }
}
