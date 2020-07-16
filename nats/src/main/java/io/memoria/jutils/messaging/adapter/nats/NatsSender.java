package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.messaging.MsgSender;
import io.memoria.jutils.core.messaging.Response;
import io.nats.client.Connection;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static io.memoria.jutils.messaging.adapter.nats.NatsUtils.toSubject;

public record NatsSender(Connection nc, MessageFilter mf, Scheduler scheduler, Duration timeout) implements MsgSender {

  @Override
  public Flux<Response> apply(Flux<Message> msgFlux) {
    return msgFlux.publishOn(scheduler).timeout(timeout).map(this::publish);
  }

  private Response publish(Message msg) {
    var subj = toSubject(mf.topic(), mf.partition());
    var msgStr = msg.value().getBytes(StandardCharsets.UTF_8);
    nc.publish(subj, msgStr);
    return Response.empty();
  }
}
