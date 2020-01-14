package com.marmoush.jutils.domain.value.msg;

import avro.shaded.com.google.common.collect.Maps;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import java.time.LocalDateTime;
import java.util.Objects;

public class PubResp {
  public final String topic;
  public final String partition;
  public final Option<Long> offset;
  public final Option<LocalDateTime> deliveryTime;
  public final Map<String, String> meta;

  public PubResp(String topic, String partition) {
    this(topic, partition, HashMap.empty(), Option.none(), Option.none());
  }

  public PubResp(String topic, String partition, Map<String, String> meta) {
    this(topic, partition, meta, Option.none(), Option.none());
  }

  public PubResp(String topic,
                 String partition,
                 Map<String, String> meta,
                 Option<Long> offset,
                 Option<LocalDateTime> deliveryTime) {
    this.topic = topic;
    this.partition = partition;
    this.meta = meta;
    this.offset = offset;
    this.deliveryTime = deliveryTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PubResp pubResp = (PubResp) o;
    return topic.equals(pubResp.topic) && partition.equals(pubResp.partition) && offset.equals(pubResp.offset) &&
           deliveryTime.equals(pubResp.deliveryTime) && meta.equals(pubResp.meta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(topic, partition, offset, deliveryTime, meta);
  }
}
