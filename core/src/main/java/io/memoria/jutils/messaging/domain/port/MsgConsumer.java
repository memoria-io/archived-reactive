package io.memoria.jutils.messaging.domain.port;

import io.memoria.jutils.messaging.domain.entity.Msg;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MsgConsumer {
  Flux<Msg> consume(String topicId, String partition, long offset);

  Mono<Void> close();
}
