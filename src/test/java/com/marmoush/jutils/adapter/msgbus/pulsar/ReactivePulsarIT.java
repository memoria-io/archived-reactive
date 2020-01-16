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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.pulsar.client.api.*;

import static io.vavr.control.Option.some;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReactivePulsarIT {

  private final YamlConfigMap config;
  private Flux<Msg> msgs;

  public ReactivePulsarIT() throws PulsarClientException {
    config = YamlUtils.parseYamlResource("pulsar.yaml").get();
    msgs = Flux.interval(Duration.ofMillis(10)).log().map(i -> new Msg("Msg number" + i, some(i + "")));
  }

  @Test
  @DisplayName("Should produce messages and consume them correctly")
  public void produceAndConsume()
          throws PulsarClientException, InterruptedException, ExecutionException, TimeoutException {
    final String TOPIC = "topic-" + new Random().nextInt(1000);
    final String PARTITION = "0";

    var producer = new PulsarMsgProducer(config);
    StepVerifier.create(producer.produce(TOPIC, PARTITION, msgs.take(3)))
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectComplete()
                .verify();
    StepVerifier.create(producer.close()).expectComplete().verify();

    var consumer = new PulsarMsgConsumer(config);
    StepVerifier.create(consumer.consume(TOPIC, PARTITION, 0)).expectNextMatches(s -> {
      System.out.println(s.get().t.get().hasOrderingKey());
      return true;
    }).expectNextMatches(s -> {
      System.out.println(s.get().t.get().getKey());
      return true;
    }).expectNextMatches(s -> {
      System.out.println(s.get().t.get().getSequenceId());
      return true;
    }).thenCancel().verify();
  }
}
