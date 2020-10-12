package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.Properties;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

public class PulsarUtils {

  public static Consumer<String> createConsumer(PulsarClient client, MessageFilter mf) throws PulsarClientException {
    var consumer = client.newConsumer(Schema.STRING)
                         .topic(mf.topic())
                         .subscriptionName(mf.topic() + "subscription")
                         .subscribe();
    consumer.seek(mf.offset());
    return consumer;
  }

  public static Producer<String> createProducer(PulsarClient client, MessageFilter mf) throws PulsarClientException {
    return client.newProducer(Schema.STRING).topic(mf.topic()).create();
  }

  public static PulsarClient pulsarClient(Properties map) throws PulsarClientException {
    var config = map.sub("pulsar").get().asString("serviceUrl").get();
    return PulsarClient.builder().serviceUrl(config).build();
  }

  private PulsarUtils() {}
}
