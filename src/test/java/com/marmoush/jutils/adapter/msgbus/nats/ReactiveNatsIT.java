package com.marmoush.jutils.adapter.msgbus.nats;

import com.marmoush.jutils.adapter.msgbus.Nats.NatsConnection;
import com.marmoush.jutils.adapter.msgbus.Nats.NatsMsgConsumer;
import com.marmoush.jutils.adapter.msgbus.Nats.NatsMsgProducer;
import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.port.msgbus.MsgProducer;
import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.ProducerResp;
import com.marmoush.jutils.utils.yaml.YamlUtils;
import io.nats.client.Connection;
import io.vavr.collection.Map;
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
  private final Map<String, Object> config = YamlUtils.parseYamlResource("nats.yaml").get();
  private final String PARTITION = "0";
  private final int MSG_COUNT = 3;

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void NatsPubSub() throws IOException, InterruptedException, TimeoutException {
    final var TOPIC = "topic-" + new Random().nextInt(1000);
    var nc = NatsConnection.create(config);
    var msgProducer = new NatsMsgProducer(nc, Schedulers.elastic(), Duration.ofMillis(500));
    var msgConsumer = new NatsMsgConsumer(nc, Schedulers.elastic(), Duration.ofMillis(500));

    var msgs = Flux.interval(Duration.ofMillis(10)).map(i -> new Msg("Msg number" + i, some(i + ""))).take(MSG_COUNT);
    var publisher = msgProducer.produce(TOPIC, PARTITION, msgs);
    var consumer = msgConsumer.consume(TOPIC, PARTITION, 0).take(MSG_COUNT);

    StepVerifier.create(publisher)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectNextMatches(Try::isSuccess)
                .expectComplete()
                .verify();
    nc.flush(Duration.ofMillis(100));
    StepVerifier.create(consumer)
                .expectNextMatches(s -> s.get().msg.value.equals("Msg number" + 0))
                .expectNextMatches(s -> s.get().msg.value.equals("Msg number" + 1))
                .expectNextMatches(s -> s.get().msg.value.equals("Msg number" + 2))
                .expectComplete()
                .verify();
    nc.close();
  }
}
