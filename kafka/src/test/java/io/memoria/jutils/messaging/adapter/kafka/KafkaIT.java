package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.core.utils.yaml.YamlUtils;
import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.MessageFilter;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import io.vavr.Function1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Random;

import static io.memoria.jutils.messaging.adapter.kafka.KafkaUtils.kafkaConsumer;
import static io.memoria.jutils.messaging.adapter.kafka.KafkaUtils.kafkaProducer;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.requireNonNull;
import static reactor.core.scheduler.Schedulers.elastic;

public class KafkaIT {
  private static final Function1<String, Mono<YamlConfigMap>> YAML_RESOURCE_PARSER = YamlUtils.parseYamlResource(
          Schedulers.elastic());
  private static final MessageFilter mf = new MessageFilter("topic-" + new Random().nextInt(1000), 0, 0);
  private static final int MSG_COUNT = 10;

  private final YamlConfigMap config;
  private final MsgSender msgSender;
  private final MsgReceiver msgReceiver;
  private final Flux<Message> msgs;

  public KafkaIT() {
    config = requireNonNull(YAML_RESOURCE_PARSER.apply("kafka.yaml").block());
    msgSender = new KafkaMsgSender(kafkaProducer(config), mf, elastic(), ofSeconds(1));
    // TODO check why Schedulers.elastic() causes multithreading error with Kafka
    msgReceiver = new KafkaMsgReceiver(kafkaConsumer(config), mf, Schedulers.immediate(), ofSeconds(1));
    msgs = Flux.interval(ofMillis(10)).map(i -> new Message("Msg number" + i).withId(i));
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void kafkaPubSub() {
    var publisher = msgSender.apply(msgs.take(MSG_COUNT));
    var consumer = msgReceiver.get().take(MSG_COUNT);
    StepVerifier.create(publisher).expectNextCount(MSG_COUNT).expectComplete().verify();
    StepVerifier.create(consumer).expectNextCount(MSG_COUNT).expectComplete().verify();
  }
}
