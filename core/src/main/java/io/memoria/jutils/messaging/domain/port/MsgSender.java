package io.memoria.jutils.messaging.domain.port;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.Response;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface MsgSender {
  Flux<Response> send(String topic, int partition, Flux<Message> msgFlux);
}
