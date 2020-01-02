package com.marmoush.jutils.domain.port.msgbus;

import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.PublishResponse;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

public interface MsgPublisher {
  Mono<Try<PublishResponse>> publish(String topic, int partition, Msg msg);
}
