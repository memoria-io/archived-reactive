package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.utils.yaml.YamlUtils;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Random;

import static io.vavr.control.Option.some;

public class ReactiveKafkaIT {
  private final Map<String, Object> config;

  private final LinkedHashMap<String, Object> producerConf;
  private final KafkaProducer<String, String> kafkaProducer;
  private final KafkaMsgProducer msgProducer;

  private final LinkedHashMap<String, Object> consumerConf;
  private final KafkaConsumer<String, String> kafkaConsumer;
  private final KafkaMsgConsumer msgConsumer;

  public ReactiveKafkaIT() {
    config = YamlUtils.parseYamlResource("kafka.yaml").get();
    // Publisher
    //noinspection unchecked
    producerConf = (LinkedHashMap<String, Object>) config.get("producer").get();
    kafkaProducer = new KafkaProducer<>(producerConf);
    msgProducer = new KafkaMsgProducer(kafkaProducer, Schedulers.elastic(), Duration.ofSeconds(1));
    //noinspection unchecked
    consumerConf = (LinkedHashMap<String, Object>) config.get("consumer").get();
    kafkaConsumer = new KafkaConsumer<>(consumerConf);
    msgConsumer = new KafkaMsgConsumer(kafkaConsumer, Schedulers.elastic(), Duration.ofSeconds(1));
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void kafkaPubSub() {
    final String TOPIC = "topic-" + new Random().nextInt(1000);
    final String PARTITION = "0";
    final int MSG_COUNT = 3;

    var msgs = Flux.interval(Duration.ofMillis(10)).map(i -> new Msg("Msg number" + i, some(i + ""))).take(MSG_COUNT);
    var publisher = msgProducer.produce(TOPIC, PARTITION, msgs);
    var consumer = msgConsumer.consume(TOPIC, PARTITION, 0).take(MSG_COUNT);

    StepVerifier.create(publisher)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectComplete()
                .verify();
    StepVerifier.create(consumer)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(pr -> pr.get().msg.pkey.equals(some("2")))
                .expectComplete()
                .verify();
  }
}
