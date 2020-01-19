package com.marmoush.jutils.domain.value.msg;

import java.time.LocalDateTime;
import java.util.Objects;

public class ProducerResp<T> {
  public final T response;
  public final LocalDateTime publishTime;

  public ProducerResp(T response) {
    this(response, LocalDateTime.now());
  }

  public ProducerResp(T response, LocalDateTime publishTime) {
    this.response = response;
    this.publishTime = publishTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ProducerResp<?> that = (ProducerResp<?>) o;
    return response.equals(that.response) && publishTime.equals(that.publishTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(response, publishTime);
  }
}
