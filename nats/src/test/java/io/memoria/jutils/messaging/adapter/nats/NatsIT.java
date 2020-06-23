package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.core.utils.yaml.YamlUtils;
import io.memoria.jutils.messaging.domain.Message;
import io.nats.client.Connection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.Step;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;

import static io.vavr.API.Some;
import static java.lang.System.out;

public class NatsIT {
  private final YamlConfigMap config = YamlUtils.parseYamlResource("nats.yaml").block();
  private final Connection nc;
  private final NatsMsgSender msgSender;
  private final NatsMsgReceiver msgReceiver;
  private final Flux<Message> msgs;
  private final String TOPIC = "topic-" + new Random().nextInt(1000);
  private final String PARTITION = "0";
  private final String subject = NatsUtils.toSubject(TOPIC, PARTITION);
  private final int MSG_COUNT = 10;

  @AfterEach
  public void afterEach() throws InterruptedException {
    nc.close();
  }

  public NatsIT() throws IOException, InterruptedException {
    assert config != null;
    nc = NatsUtils.createConnection(config);
    msgSender = new NatsMsgSender(nc, Schedulers.elastic(), Duration.ofSeconds(1));
    msgReceiver = new NatsMsgReceiver(nc, Schedulers.elastic(), Duration.ofSeconds(1));
    msgs = Flux.interval(Duration.ofMillis(10)).map(i -> new Message(Some(i + ""), "Msg number" + i));
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void NatsPubSub() throws InterruptedException {
    var sender = msgSender.send(TOPIC, PARTITION, msgs.take(MSG_COUNT));
    var receiver = msgReceiver.receive(TOPIC, PARTITION, 0).doOnNext(out::println).take(MSG_COUNT);
    var t = new Thread(() -> StepVerifier.create(sender).expectNextCount(MSG_COUNT).expectComplete().verify());
    t.start();
    StepVerifier.create(receiver).expectNextCount(MSG_COUNT / 2).thenCancel().verify();
    t.join();
  }
}
