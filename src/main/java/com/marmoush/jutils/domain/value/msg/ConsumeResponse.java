package com.marmoush.jutils.domain.value.msg;

import io.vavr.control.Option;

import java.time.LocalDateTime;

public class ConsumeResponse extends PublishResponse {
  public final Msg msg;

  public ConsumeResponse(Msg msg,
                         String topic,
                         String partition,
                         Option<Long> offset,
                         Option<LocalDateTime> deliveryTime) {
    super(topic, partition, offset, deliveryTime);
    this.msg = msg;
  }
}
