package com.marmoush.jutils.messaging.adapter.msgbus.pulsar;

import com.marmoush.jutils.messaging.domain.entity.Msg;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import com.marmoush.jutils.utils.yaml.YamlUtils;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Random;

public class ReactivePulsarIT {

  private final YamlConfigMap config;
  private Flux<Msg> msgs;

  public ReactivePulsarIT() {
    config = YamlUtils.parseYamlResource("pulsar.yaml").get();
    msgs = Flux.interval(Duration.ofMillis(10)).log().map(i -> new Msg(i + "", "Msg number" + i));
  }

  @Test
  @DisplayName("Should produce messages and consume them correctly")
  public void produceAndConsume() throws PulsarClientException {
    final String TOPIC = "topic-" + new Random().nextInt(1000);
    final String PARTITION = "0";

    var producer = new PulsarMsgProducer(config);
    StepVerifier.create(producer.produce(TOPIC, PARTITION, msgs.take(3)))
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectComplete()
                .verify();
    producer.close().subscribe();
    var consumer = new PulsarMsgConsumer(config);
    var c = consumer.consume(TOPIC, PARTITION, 0);
    StepVerifier.create(c.take(3)).expectNextCount(2).expectNextMatches(Try::isSuccess).expectComplete().verify();
    consumer.close().subscribe();
  }
}

