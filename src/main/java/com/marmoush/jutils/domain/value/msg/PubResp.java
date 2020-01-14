package com.marmoush.jutils.domain.value.msg;

import io.vavr.control.Option;

import java.time.LocalDateTime;
import java.util.Objects;

public class PubResp {
  public final String topic;
  public final String partition;
  public final Option<Long> offset;
  public final Option<LocalDateTime> deliveryTime;

  public PubResp(String topic, String partition) {
    this(topic, partition, Option.none(), Option.none());
  }

  public PubResp(String topic, String partition, Option<Long> offset, Option<LocalDateTime> deliveryTime) {
    this.topic = topic;
    this.partition = partition;
    this.offset = offset;
    this.deliveryTime = deliveryTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PubResp that = (PubResp) o;
    return partition.equals(that.partition) && topic.equals(that.topic) && offset.equals(that.offset) &&
           deliveryTime.equals(that.deliveryTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(topic, partition, offset, deliveryTime);
  }
}
