package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.core.utils.yaml.YamlUtils;
import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Random;

import static io.memoria.jutils.core.utils.yaml.YamlUtils.parseYamlResource;
import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.pulsarClient;
import static io.vavr.API.Some;
import static java.util.Objects.requireNonNull;

public class PulsarIT {
  private static final String TOPIC = "topic-" + new Random().nextInt(1000);
  private static final int PARTITION = 0;
  private static final int MSG_COUNT = 10;

  private final YamlConfigMap config;
  private final PulsarClient client;
  private final MsgSender msgSender;
  private final MsgReceiver msgReceiver;
  private final Flux<Message> msgs;

  public PulsarIT() throws PulsarClientException {
    config = requireNonNull(parseYamlResource("pulsar.yaml").block());
    client = pulsarClient(config);
    msgSender = new PulsarMsgSender(client, Duration.ofSeconds(1));
    msgReceiver = new PulsarMsgReceiver(client, Duration.ofSeconds(1));
    msgs = Flux.interval(Duration.ofMillis(10)).log().map(i -> new Message(Some(i + ""), "Msg number" + i));
  }

  @Test
  @DisplayName("Should produce messages and consume them correctly")
  public void produceAndConsume() {
    StepVerifier.create(msgSender.send(TOPIC, PARTITION, msgs.take(MSG_COUNT)))
                .expectNextCount(MSG_COUNT)
                .expectComplete()
                .verify();
    StepVerifier.create(msgReceiver.receive(TOPIC, PARTITION, 0).take(MSG_COUNT))
                .expectNextCount(MSG_COUNT)
                .expectComplete()
                .verify();
  }
}

