package com.marmoush.jutils.domain.port.msgbus;

import com.marmoush.jutils.domain.value.msg.SubResp;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

public interface MsgSub {
  Flux<Try<SubResp>> sub(String topicId, String partition, long offset);
}
