package com.marmoush.jutils.domain.value.msg;

import io.vavr.control.Option;

import java.util.Objects;

import static io.vavr.control.Option.none;

public class ProducerResp<T> {
  public final Option<Long> offset;
  public final Option<T> t;

  public ProducerResp() {
    this(none(), none());
  }

  public ProducerResp(Option<Long> offset, Option<T> t) {
    this.offset = offset;
    this.t = t;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ProducerResp<?> producerResp = (ProducerResp<?>) o;
    return Objects.equals(offset, producerResp.offset) && Objects.equals(t, producerResp.t);
  }

  @Override
  public int hashCode() {
    return Objects.hash(offset, t);
  }
}
