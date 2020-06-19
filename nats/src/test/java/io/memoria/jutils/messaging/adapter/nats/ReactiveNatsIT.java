package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.core.utils.yaml.YamlUtils;
import io.memoria.jutils.messaging.domain.entity.Msg;
import io.vavr.control.Try;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;

public class ReactiveNatsIT {
  private final YamlConfigMap config;
  private final NatsMsgProducer msgProducer;
  private final NatsMsgConsumer msgConsumer;
  private final Flux<Msg> msgs;

  public ReactiveNatsIT() throws IOException, InterruptedException {
    config = YamlUtils.parseYamlResource("nats.yaml").block();
    msgProducer = NatsUtils.natsMsgProducer(config, Schedulers.elastic());
    msgConsumer = NatsUtils.natsMsgConsumer(config, Schedulers.elastic());
    msgs = Flux.interval(Duration.ofMillis(10)).map(i -> new Msg(i + "", "Msg number" + i));
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void NatsPubSub() {
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
                .expectNextMatches(s -> s.get().value().equals("Msg number" + 0))
                .expectNextMatches(s -> s.get().value().equals("Msg number" + 1))
                .expectNextMatches(s -> s.get().value().equals("Msg number" + 2))
                .expectComplete()
                .verify();
    msgConsumer.close().subscribe();
  }
}
