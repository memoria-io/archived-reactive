package com.marmoush.jutils.adapter.msgbus.pulsar;

import io.vavr.collection.Map;
import org.apache.pulsar.client.api.*;

public class Pulsar {
  public static PulsarClient client(Map<String, Object> configMap) throws PulsarClientException {
    String url = (String) configMap.get("pulsar.serviceUrl").get();
    return PulsarClient.builder().serviceUrl(url).build();
  }

  public static Producer<String> producer(PulsarClient client, String topic) throws PulsarClientException {
    return client.newProducer(Schema.STRING).topic(topic).create();
  }

  public static Consumer<String> consumer(PulsarClient client, String topic) throws PulsarClientException {
    return client.newConsumer(Schema.STRING).topic(topic).subscribe();
  }
}
