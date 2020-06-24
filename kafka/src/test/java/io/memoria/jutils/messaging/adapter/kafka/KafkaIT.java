package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.core.utils.yaml.YamlUtils;
import io.memoria.jutils.messaging.domain.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Random;

import static io.memoria.jutils.messaging.adapter.kafka.KafkaUtils.kafkaConsumer;
import static io.memoria.jutils.messaging.adapter.kafka.KafkaUtils.kafkaProducer;
import static io.vavr.API.Some;

public class KafkaIT {
  private static final YamlConfigMap config = YamlUtils.parseYamlResource("kafka.yaml").block();

  private final KafkaMsgSender msgSender;
  private final KafkaMsgReceiver msgReceiver;
  private final Flux<Message> msgs;

  public KafkaIT() {
    msgSender = new KafkaMsgSender(kafkaProducer(config), Schedulers.elastic(), Duration.ofSeconds(1));
    msgReceiver = new KafkaMsgReceiver(kafkaConsumer(config), Schedulers.elastic(), Duration.ofSeconds(1));
    msgs = Flux.interval(Duration.ofMillis(10)).map(i -> new Message(Some(i + ""), "Msg number" + i));
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void kafkaPubSub() {
    final String TOPIC = "topic-" + new Random().nextInt(1000);
    final int PARTITION = 0;
    final int MSG_COUNT = 10;

    var publisher = msgSender.send(TOPIC, PARTITION, msgs.take(MSG_COUNT));
    var consumer = msgReceiver.receive(TOPIC, PARTITION, 0).take(MSG_COUNT);

    StepVerifier.create(publisher).expectNextCount(MSG_COUNT).expectComplete().verify();
    StepVerifier.create(consumer).expectNextCount(MSG_COUNT).expectComplete().verify();
  }
}
