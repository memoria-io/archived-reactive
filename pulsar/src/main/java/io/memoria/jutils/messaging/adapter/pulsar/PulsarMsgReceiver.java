package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.consume;
import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.createConsumer;
import static java.util.function.Function.identity;

public class PulsarMsgReceiver implements MsgReceiver {
  private final PulsarClient client;

  public PulsarMsgReceiver(PulsarClient client) {
    this.client = client;
  }

  @Override
  public Flux<Message> receive(String topicId, int partition, long offset) {
    try {
      var consumer = createConsumer(client, topicId, offset);
      return Flux.<Flux<Message>>generate(s -> s.next(consume(consumer).flux())).flatMap(identity());
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
    PulsarMsgReceiver that = (PulsarMsgReceiver) o;
    return client.equals(that.client);
  }

  @Override
  public int hashCode() {
    return Objects.hash(client);
  }
}
