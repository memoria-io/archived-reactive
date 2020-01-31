package com.marmoush.jutils.messaging.domain.port;

import com.marmoush.jutils.messaging.domain.entity.Msg;
import io.vavr.control.Try;
import reactor.core.publisher.*;

public interface MsgConsumer {
  Flux<Try<Msg>> consume(String topicId, String partition, long offset);

  Mono<Try<Void>> close();
}
