package io.memoria.jutils.messaging.domain.port;

import io.memoria.jutils.messaging.domain.entity.Msg;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MsgProducer {
  Flux<Try<Void>> produce(String topic, String partition, Flux<Msg> msgFlux);

  Mono<Try<Void>> close();
}
