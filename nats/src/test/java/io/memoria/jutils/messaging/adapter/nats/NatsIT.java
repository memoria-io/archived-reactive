package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.messaging.MsgReceiver;
import io.memoria.jutils.core.messaging.MsgSender;
import io.nats.client.Connection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.requireNonNull;
import static reactor.core.scheduler.Schedulers.elastic;

class NatsIT {
  private static final MessageFilter mf = new MessageFilter("topic-" + new Random().nextInt(1000), 0, 0);
  private static final int MSG_COUNT = 10;

  private final Connection nc;
  private final MsgSender msgSender;
  private final MsgReceiver msgReceiver;
  private final Flux<Message> infiniteMsgsFlux;
  private final Flux<Message> limitedMsgsFlux;
  private final Message[] limitedMsgsArr;

  NatsIT() throws IOException, InterruptedException {
    var duration = Duration.ofMillis(1000);
    nc = NatsUtils.createConnection("nats://localhost:4222", duration, duration, 1000, Duration.ofMillis(1000));
    msgSender = new NatsSender(nc, mf, elastic(), ofSeconds(1));
    msgReceiver = new NatsReceiver(nc, mf, elastic(), ofSeconds(1));
    infiniteMsgsFlux = Flux.interval(ofMillis(100)).map(this::iToMessage);
    limitedMsgsFlux = infiniteMsgsFlux.take(MSG_COUNT);
    limitedMsgsArr = requireNonNull(limitedMsgsFlux.collectList().block()).toArray(new Message[0]);
  }

  @Test
  @DisplayName("Consumed messages should be same as published ones.")
  void NatsPubSub() {
    var sender = msgSender.apply(limitedMsgsFlux);
    var receiver = msgReceiver.get().take(MSG_COUNT);
    StepVerifier.create(sender.zipWith(receiver)).expectNextCount(MSG_COUNT).expectComplete().verify();
  }

  @AfterEach
  void afterEach() throws InterruptedException {
    nc.close();
  }

  private Message iToMessage(long i) {
    return new Message("Msg number" + i).withId(i);
  }
}
