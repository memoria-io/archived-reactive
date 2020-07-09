package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.core.utils.file.DefaultReactiveFileReader;
import io.memoria.jutils.core.utils.file.ReactiveFileReader;
import io.memoria.jutils.core.utils.file.YamlConfigMap;
import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.MessageFilter;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Random;

import static io.memoria.jutils.core.utils.file.ReactiveFileReader.resourcePath;
import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.createConsumer;
import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.createProducer;
import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.pulsarClient;

public class PulsarIT {
  private static final ReactiveFileReader reader = new DefaultReactiveFileReader(Schedulers.boundedElastic());
  private static final YamlConfigMap config = reader.yaml(resourcePath("pulsar.yaml").get()).block();

  private static final MessageFilter mf = new MessageFilter("topic-" + new Random().nextInt(1000), 0, 0);
  private static final int MSG_COUNT = 10;

  private final PulsarClient client;
  private final MsgSender msgSender;
  private final MsgReceiver msgReceiver;
  private final Flux<Message> msgs;

  public PulsarIT() throws PulsarClientException {
    client = pulsarClient(config);
    msgSender = new PulsarMsgSender(createProducer(client, mf));
    msgReceiver = new PulsarMsgReceiver(createConsumer(client, mf));
    msgs = Flux.interval(Duration.ofMillis(10)).log().map(i -> new Message("Msg number" + i).withId(i));
  }

  @Test
  @DisplayName("Should produce messages and consume them correctly")
  public void produceAndConsume() {
    StepVerifier.create(msgSender.apply(msgs.take(MSG_COUNT))).expectNextCount(MSG_COUNT).expectComplete().verify();
    StepVerifier.create(msgReceiver.get().take(MSG_COUNT)).expectNextCount(MSG_COUNT).expectComplete().verify();
  }
}

