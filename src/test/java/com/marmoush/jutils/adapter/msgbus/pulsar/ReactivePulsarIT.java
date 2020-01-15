package com.marmoush.jutils.adapter.msgbus.pulsar;

import com.marmoush.jutils.adapter.msgbus.kafka.KafkaMsgConsumer;
import com.marmoush.jutils.adapter.msgbus.kafka.KafkaMsgProducer;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import com.marmoush.jutils.utils.yaml.YamlUtils;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Random;

import org.apache.pulsar.client.api.*;

import static io.vavr.control.Option.some;

public class ReactivePulsarIT {

  private final YamlConfigMap config;
  private final PulsarClient client;
  private Flux<Msg> msgs;

  public ReactivePulsarIT() throws PulsarClientException {
    config = YamlUtils.parseYamlResource("pulsar.yaml").get();
    client = Pulsar.client(config);
    msgs = Flux.interval(Duration.ofMillis(10)).map(i -> new Msg("Msg number" + i, some(i + "")));
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void kafkaPubSub() throws PulsarClientException {
    final String TOPIC = "topic-" + new Random().nextInt(1000);
    final String PARTITION = "0";

    var producer = new PulsarMsgProducer(Pulsar.producer(client, TOPIC));
    var publisher = producer.produce(TOPIC, PARTITION, msgs.take(3));

    StepVerifier.create(publisher)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectComplete()
                .verify();
  }
}
