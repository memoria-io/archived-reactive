package com.marmoush.jutils.domain.port.msgbus;

import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.ProducerResp;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

public interface MsgProducer<T> {
  Flux<Try<ProducerResp<T>>> produce(String topic, String partition, Flux<Msg> msgFlux);
}
