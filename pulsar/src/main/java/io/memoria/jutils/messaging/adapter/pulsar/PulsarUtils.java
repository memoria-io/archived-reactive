package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.time.Duration;

public class PulsarUtils {
  private PulsarUtils() {}

  public static PulsarMsgConsumer pulsarMsgConsumer(YamlConfigMap map) throws PulsarClientException {
    return new PulsarMsgConsumer(PulsarClient.builder()
                                             .serviceUrl(map.asYamlConfigMap("pulsar").asString("serviceUrl"))
                                             .build(),
                                 Duration.ofMillis(map.asYamlConfigMap("reactorPulsar").asLong("request.timeout")));
  }

  public static PulsarMsgProducer pulsarMsgProducer(YamlConfigMap map) throws PulsarClientException {
    return new PulsarMsgProducer(PulsarClient.builder()
                                             .serviceUrl(map.asYamlConfigMap("pulsar").asString("serviceUrl"))
                                             .build(),
                                 Duration.ofMillis(map.asYamlConfigMap("reactorPulsar").asLong("request.timeout")));
  }
}
