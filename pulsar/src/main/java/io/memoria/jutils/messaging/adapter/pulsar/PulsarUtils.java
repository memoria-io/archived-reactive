package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.messaging.domain.Message;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import reactor.core.publisher.Mono;

import static io.vavr.API.Some;

public class PulsarUtils {
  static Mono<Message> consume(Consumer<String> c) {
    return Mono.fromFuture(c.receiveAsync()).map(m -> new Message(Some(m.getKey()), m.getValue()));
  }

  static Consumer<String> createConsumer(PulsarClient client, String topic, long offset) throws PulsarClientException {
    var consumer = client.newConsumer(Schema.STRING).topic(topic).subscriptionName(topic + "subscription").subscribe();
    consumer.seek(offset);
    return consumer;
  }

  static Producer<String> createProducer(PulsarClient client, String topic) throws PulsarClientException {
    return client.newProducer(Schema.STRING).topic(topic).create();
  }

  public static PulsarClient pulsarClient(YamlConfigMap map) throws PulsarClientException {
    var config = map.asYamlConfigMap("pulsar").get().asString("serviceUrl").get();
    return PulsarClient.builder().serviceUrl(config).build();
  }

  private PulsarUtils() {}
}
