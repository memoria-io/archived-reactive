package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.messaging.MsgSender;
import io.memoria.jutils.core.messaging.Response;
import io.nats.client.Connection;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static io.memoria.jutils.messaging.adapter.nats.NatsUtils.toSubject;

public record NatsSender(Connection nc, MessageFilter mf, Scheduler scheduler, Duration timeout) implements MsgSender {

  @Override
  public Mono<Response> apply(Message message) {
    return Mono.fromCallable(() -> publish(message)).subscribeOn(scheduler).timeout(timeout);
  }

  private Response publish(Message msg) {
    var subj = toSubject(mf.topic(), mf.partition());
    var msgStr = msg.value().getBytes(StandardCharsets.UTF_8);
    nc.publish(subj, msgStr);
    return Response.empty();
  }
}
