package com.marmoush.jutils.messaging.domain.port.msgbus;

import com.marmoush.jutils.general.domain.entity.Msg;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MsgProducer {
  Flux<Try<Void>> produce(String topic, String partition, Flux<Msg> msgFlux);

  Mono<Try<Void>> close();
}
