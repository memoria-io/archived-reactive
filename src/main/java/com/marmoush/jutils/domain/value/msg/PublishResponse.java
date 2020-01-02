package com.marmoush.jutils.domain.value.msg;

import io.vavr.control.Option;

import java.time.LocalDateTime;
import java.util.Objects;

public class PublishResponse {
  public final String topic;
  public final int partition;
  public final Option<Long> offset;
  public final Option<LocalDateTime> deliveryTime;

  public PublishResponse(String topic, int partition, Option<Long> offset, Option<LocalDateTime> deliveryTime) {
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
    PublishResponse that = (PublishResponse) o;
    return partition == that.partition && topic.equals(that.topic) && offset.equals(that.offset) &&
           deliveryTime.equals(that.deliveryTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(topic, partition, offset, deliveryTime);
  }
}
