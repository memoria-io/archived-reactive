package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.core.utils.yaml.YamlUtils;
import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.MessageFilter;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import io.nats.client.Connection;
import io.vavr.Function1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Random;

import static java.lang.System.out;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.requireNonNull;
import static reactor.core.scheduler.Schedulers.elastic;

public class NatsIT {
  private static final Function1<String, Mono<YamlConfigMap>> YAML_RESOURCE_PARSER = YamlUtils.parseYamlResource(
          Schedulers.elastic());
  private static final MessageFilter mf = new MessageFilter("topic-" + new Random().nextInt(1000), 0, 0);
  private static final int MSG_COUNT = 10;

  private final YamlConfigMap config;
  private final Connection nc;
  private final MsgSender msgSender;
  private final MsgReceiver msgReceiver;
  private final Flux<Message> msgs;

  public NatsIT() throws IOException, InterruptedException {
    config = requireNonNull(YAML_RESOURCE_PARSER.apply("nats.yaml").block());
    nc = NatsUtils.createConnection(config);
    msgSender = new NatsMsgSender(nc, mf, elastic(), ofSeconds(1));
    msgReceiver = new NatsMsgReceiver(nc, mf, elastic(), ofSeconds(1));
    msgs = Flux.interval(ofMillis(10)).map(i -> new Message("Msg number" + i).withId(i));
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  public void NatsPubSub() throws InterruptedException {
    var sender = msgSender.apply(msgs.take(MSG_COUNT));
    var receiver = msgReceiver.get().doOnNext(out::println).take(MSG_COUNT);
    var t = new Thread(() -> StepVerifier.create(sender).expectNextCount(MSG_COUNT).expectComplete().verify());
    t.start();
    StepVerifier.create(receiver).expectNextCount(MSG_COUNT / 2).thenCancel().verify();
    t.join();
  }

  @AfterEach
  public void afterEach() throws InterruptedException {
    nc.close();
  }
}
