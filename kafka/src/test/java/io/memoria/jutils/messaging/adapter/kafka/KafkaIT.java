package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.core.utils.yaml.YamlUtils;
import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Objects;
import java.util.Random;

import static io.memoria.jutils.messaging.adapter.kafka.KafkaUtils.kafkaConsumer;
import static io.memoria.jutils.messaging.adapter.kafka.KafkaUtils.kafkaProducer;
import static io.vavr.API.Some;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.requireNonNull;
import static reactor.core.scheduler.Schedulers.elastic;

public class KafkaIT {
  private static final String TOPIC = "topic-" + new Random().nextInt(1000);
  private static final int PARTITION = 0;
  private static final int MSG_COUNT = 10;

  private final YamlConfigMap config;
  private final MsgSender msgSender;
  private final MsgReceiver msgReceiver;
  private final Flux<Message> msgs;

  public KafkaIT() {
    config = requireNonNull(YamlUtils.parseYamlResource("kafka.yaml").block());
    msgSender = new KafkaMsgSender(kafkaProducer(config), elastic(), ofSeconds(1));
    msgReceiver = new KafkaMsgReceiver(kafkaConsumer(config), elastic(), ofSeconds(1));
    msgs = Flux.interval(ofMillis(10)).map(i -> new Message(Some(i + ""), "Msg number" + i));
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void kafkaPubSub() {
    var publisher = msgSender.send(TOPIC, PARTITION, msgs.take(MSG_COUNT));
    var consumer = msgReceiver.receive(TOPIC, PARTITION, 0).take(MSG_COUNT);
    StepVerifier.create(publisher).expectNextCount(MSG_COUNT).expectComplete().verify();
    StepVerifier.create(consumer).expectNextCount(MSG_COUNT).expectComplete().verify();
  }
}
