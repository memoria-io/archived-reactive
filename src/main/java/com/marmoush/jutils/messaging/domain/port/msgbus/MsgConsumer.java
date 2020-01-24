package com.marmoush.jutils.messaging.domain.port.msgbus;

import com.marmoush.jutils.general.domain.entity.Msg;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MsgConsumer {
  Flux<Try<Msg>> consume(String topicId, String partition, long offset);

  Mono<Try<Void>> close();
}
