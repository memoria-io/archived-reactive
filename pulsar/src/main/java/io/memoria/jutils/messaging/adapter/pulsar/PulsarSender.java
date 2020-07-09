package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.Response;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record PulsarSender(Producer<String>producer) implements MsgSender {

  @Override
  public Flux<Response> apply(Flux<Message> msgFlux) {
    return msgFlux.concatMap(this::send)
                  .map(MessageId::toByteArray)
                  .map(String::new)
                  .map(id -> new Response().withReply(id));
  }

  private Mono<MessageId> send(Message message) {
    var pm = producer.newMessage();
    if (message.id().isDefined()) {
      pm = pm.sequenceId(message.id().get());
    }
    return Mono.fromFuture(pm.value(message.value()).sendAsync());
  }
}
