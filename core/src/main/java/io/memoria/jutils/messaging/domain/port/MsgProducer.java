package io.memoria.jutils.messaging.domain.port;

import io.memoria.jutils.messaging.domain.entity.Msg;
import io.memoria.jutils.messaging.domain.entity.Response;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MsgProducer {
  Flux<Response> produce(String topic, String partition, Flux<Msg> msgFlux);

  Mono<Void> close();
}
