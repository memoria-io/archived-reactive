package io.memoria.jutils.messaging.domain.port;

import io.memoria.jutils.messaging.domain.Message;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface MsgSender {
  Flux<Option<Message>> send(String topic, String partition, Flux<Message> msgFlux);
}
