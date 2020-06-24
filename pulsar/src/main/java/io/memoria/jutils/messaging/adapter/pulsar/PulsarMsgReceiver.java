package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.*;
import static java.util.function.Function.identity;

public record PulsarMsgReceiver(PulsarClient client, Duration timeout) implements MsgReceiver {

  @Override
  public Flux<Message> receive(String topicId, int partition, long offset) {
    try {
      var consumer = createConsumer(client, topicId, offset);
      return Flux.<Flux<Message>>generate(s -> s.next(consume(consumer).flux())).flatMap(identity());
    } catch (PulsarClientException e) {
      return Flux.error(e);
    }
  }
}
