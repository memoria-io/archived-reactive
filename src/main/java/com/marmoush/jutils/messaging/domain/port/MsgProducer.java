package com.marmoush.jutils.messaging.domain.port;

import com.marmoush.jutils.messaging.domain.entity.Msg;
import io.vavr.control.Try;
import reactor.core.publisher.*;

public interface MsgProducer {
  Flux<Try<Void>> produce(String topic, String partition, Flux<Msg> msgFlux);

  Mono<Try<Void>> close();
}
