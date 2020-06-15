package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.core.utils.yaml.YamlUtils;
import io.memoria.jutils.messaging.domain.entity.Msg;
import io.vavr.control.Try;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Random;

public class ReactiveKafkaIT {
  private final YamlConfigMap config;

  private final KafkaMsgProducer msgProducer;
  private final KafkaMsgConsumer msgConsumer;
  private final Flux<Msg> msgs;

  public ReactiveKafkaIT() {
    config = YamlUtils.parseYamlResource("kafka.yaml").get();
    msgProducer = KafkaUtils.kafkaMsgProducer(config, Schedulers.elastic());
    msgConsumer = KafkaUtils.kafkaMsgConsumer(config, Schedulers.elastic());
    msgs = Flux.interval(Duration.ofMillis(10)).map(i -> new Msg(i + "", "Msg number" + i));
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void kafkaPubSub() {
    final String TOPIC = "topic-" + new Random().nextInt(1000);
    final String PARTITION = "0";
    final int MSG_COUNT = 3;

    var publisher = msgProducer.produce(TOPIC, PARTITION, msgs.take(MSG_COUNT));
    var consumer = msgConsumer.consume(TOPIC, PARTITION, 0).take(MSG_COUNT);

    StepVerifier.create(publisher)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectComplete()
                .verify();
    msgProducer.close().subscribe();

    StepVerifier.create(consumer).expectNextCount(3).expectComplete().verify();
    msgConsumer.close().subscribe();
  }
}
