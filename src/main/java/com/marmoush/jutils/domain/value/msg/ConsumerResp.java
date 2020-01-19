package com.marmoush.jutils.domain.value.msg;

import java.time.LocalDateTime;
import java.util.Objects;

public class ConsumerResp<T> {
  public final T response;
  public final LocalDateTime consumptionTime;

  public ConsumerResp(T response) {
    this(response, LocalDateTime.now());
  }

  public ConsumerResp(T response, LocalDateTime consumptionTime) {
    this.response = response;
    this.consumptionTime = consumptionTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ConsumerResp<?> that = (ConsumerResp<?>) o;
    return response.equals(that.response) && consumptionTime.equals(that.consumptionTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(response, consumptionTime);
  }
}
