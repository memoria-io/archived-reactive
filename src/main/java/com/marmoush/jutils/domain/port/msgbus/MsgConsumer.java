package com.marmoush.jutils.domain.port.msgbus;

import com.marmoush.jutils.domain.value.msg.ConsumeResponse;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

public interface MsgConsumer {
  Flux<Try<ConsumeResponse>> consume(String topicId, String partition, long offset);
}
