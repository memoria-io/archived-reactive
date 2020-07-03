package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.Response;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record PulsarMsgSender(Producer<String>producer) implements MsgSender {

  @Override
  public Flux<Response> apply(Flux<Message> msgFlux) {
    return msgFlux.map(Message::value)
                  .map(producer::sendAsync)
                  .flatMap(Mono::fromFuture)
                  .map(MessageId::toByteArray)
                  .map(String::new)
                  .map(Response::new);
  }
}
