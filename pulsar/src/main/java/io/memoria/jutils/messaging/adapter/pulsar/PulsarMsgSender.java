package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import io.vavr.control.Option;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.createProducer;
import static io.vavr.API.Some;

public class PulsarMsgSender implements MsgSender {
  private final PulsarClient client;

  public PulsarMsgSender(PulsarClient client) {
    this.client = client;
  }

  @Override
  public Flux<Option<Message>> send(String topic, int partitionStr, Flux<Message> msgFlux) {
    try {
      var producer = createProducer(client, topic);
      return msgFlux.map(Message::message)
                    .map(producer::sendAsync)
                    .flatMap(Mono::fromFuture)
                    .map(MessageId::toByteArray)
                    .map(String::new)
                    .map(s -> Some(new Message(Some(s), s)));
    } catch (PulsarClientException e) {
      return Flux.error(e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PulsarMsgSender that = (PulsarMsgSender) o;
    return client.equals(that.client);
  }

  @Override
  public int hashCode() {
    return Objects.hash(client);
  }
}
