package com.marmoush.jutils.domain.port.msgbus;

import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.PublishResponse;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

public interface MsgPublisher {
  Flux<Try<PublishResponse>> publish(Flux<Msg> msg, String topic, int partition);
}
