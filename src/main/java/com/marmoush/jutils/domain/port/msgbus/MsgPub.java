package com.marmoush.jutils.domain.port.msgbus;

import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.PubResp;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.PulsarClientException;
import reactor.core.publisher.Flux;

public interface MsgPub {
  Flux<Try<PubResp>> pub(Flux<Msg> msgFlux, String topic, String partitionStr) throws PulsarClientException;
}
