package com.marmoush.jutils.adapter.msgbus.nats;

import com.marmoush.jutils.adapter.msgbus.Nats.NatsConnection;
import com.marmoush.jutils.adapter.msgbus.Nats.NatsMsgConsumer;
import com.marmoush.jutils.adapter.msgbus.Nats.NatsMsgProducer;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import com.marmoush.jutils.utils.yaml.YamlUtils;
import io.nats.client.Connection;
import io.vavr.control.Try;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import static io.vavr.control.Option.some;

public class ReactiveNatsIT {
  private final YamlConfigMap config;
  private final NatsMsgProducer msgProducer;
  private final NatsMsgConsumer msgConsumer;
  private final Flux<Msg> msgs;

  public ReactiveNatsIT() throws IOException, InterruptedException {
    config = YamlUtils.parseYamlResource("nats.yaml").get();
    msgProducer = new NatsMsgProducer(config, Schedulers.elastic());
    msgConsumer = new NatsMsgConsumer(config, Schedulers.elastic());
    msgs = Flux.interval(Duration.ofMillis(10)).map(i -> new Msg("Msg number" + i, some(i + "")));
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void NatsPubSub() throws TimeoutException, InterruptedException {
    final var TOPIC = "topic-" + new Random().nextInt(1000);
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
    StepVerifier.create(consumer)
                .expectNextMatches(s -> s.get().msg.value.equals("Msg number" + 0))
                .expectNextMatches(s -> s.get().msg.value.equals("Msg number" + 1))
                .expectNextMatches(s -> s.get().msg.value.equals("Msg number" + 2))
                .expectComplete()
                .verify();
    msgConsumer.close().subscribe();
  }
}
