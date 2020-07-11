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

import java.util.Random;

import static io.memoria.jutils.core.utils.file.ReactiveFileReader.resourcePath;
import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.createConsumer;
import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.createProducer;
import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.pulsarClient;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;

public class PulsarIT {
  private static final ReactiveFileReader reader = new DefaultReactiveFileReader(Schedulers.boundedElastic());
  private static final YamlConfigMap config = reader.yaml(resourcePath("pulsar.yaml").get()).block();

  private static final MessageFilter mf = new MessageFilter("topic-" + new Random().nextInt(1000), 0, 0);
  private static final int MSG_COUNT = 10;

  private final PulsarClient client;
  private final MsgSender msgSender;
  private final MsgReceiver msgReceiver;
  private final Flux<Message> infiniteMsgsFlux;
  private final Flux<Message> limitedMsgsFlux;
  private final Message[] limitedMsgsArr;

  public PulsarIT() throws PulsarClientException {
    client = pulsarClient(config);
    msgSender = new PulsarSender(createProducer(client, mf));
    msgReceiver = new PulsarReceiver(createConsumer(client, mf), ofMillis(100));

    infiniteMsgsFlux = Flux.interval(ofMillis(100)).map(this::iToMessage);
    limitedMsgsFlux = infiniteMsgsFlux.take(MSG_COUNT);
    limitedMsgsArr = requireNonNull(limitedMsgsFlux.collectList().block()).toArray(new Message[0]);
  }

  @Test
  @DisplayName("Should produce messages and consume them correctly")
  public void produceAndConsume() {
    var sender = msgSender.apply(limitedMsgsFlux);
    var receiver = msgReceiver.get().take(MSG_COUNT);
    StepVerifier.create(sender).expectNextCount(MSG_COUNT).expectComplete().verify();
    StepVerifier.create(receiver).expectNext(limitedMsgsArr).expectComplete().verify();
  }

  private Message iToMessage(long i) {
    return new Message("Msg number" + i).withId(i);
  }
}

