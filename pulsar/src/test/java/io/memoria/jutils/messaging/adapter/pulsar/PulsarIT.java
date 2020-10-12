package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.adapter.file.LocalFileReader;
import io.memoria.jutils.core.file.FileReader;
import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.messaging.MsgReceiver;
import io.memoria.jutils.core.messaging.MsgSender;
import io.memoria.jutils.core.Properties;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Random;

import static io.memoria.jutils.core.file.FileReader.resourcePath;
import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.createConsumer;
import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.createProducer;
import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.pulsarClient;
import static java.time.Duration.ofMillis;
import static java.util.Objects.requireNonNull;

class PulsarIT {
  private static final FileReader reader = new LocalFileReader(Schedulers.boundedElastic());
  private static final Properties config = reader.yaml(resourcePath("pulsar.yaml").get()).block();

  private static final MessageFilter mf = new MessageFilter("topic-" + new Random().nextInt(1000), 0, 0);
  private static final int MSG_COUNT = 10;

  private final PulsarClient client;
  private final MsgSender msgSender;
  private final MsgReceiver msgReceiver;
  private final Flux<Message> infiniteMsgsFlux;
  private final Flux<Message> limitedMsgsFlux;
  private final Message[] limitedMsgsArr;

  PulsarIT() throws PulsarClientException {
    client = pulsarClient(config);
    msgSender = new PulsarSender(createProducer(client, mf));
    msgReceiver = new PulsarReceiver(createConsumer(client, mf), ofMillis(100));

    infiniteMsgsFlux = Flux.interval(ofMillis(100)).map(this::iToMessage);
    limitedMsgsFlux = infiniteMsgsFlux.take(MSG_COUNT);
    limitedMsgsArr = requireNonNull(limitedMsgsFlux.collectList().block()).toArray(new Message[0]);
  }

  @Test
  @DisplayName("Should produce messages and consume them correctly")
  void produceAndConsume() {
    var sender = msgSender.apply(limitedMsgsFlux);
    var receiver = msgReceiver.get().take(MSG_COUNT);
    StepVerifier.create(sender).expectNextCount(MSG_COUNT).expectComplete().verify();
    StepVerifier.create(receiver).expectNext(limitedMsgsArr).expectComplete().verify();
  }

  private Message iToMessage(long i) {
    return new Message("Msg number" + i).withId(i);
  }
}

