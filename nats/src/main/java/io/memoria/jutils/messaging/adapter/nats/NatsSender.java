package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.MessageFilter;
import io.memoria.jutils.messaging.domain.Response;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import io.nats.client.Connection;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static io.memoria.jutils.messaging.adapter.nats.NatsUtils.toSubject;

public record NatsSender(Connection nc, MessageFilter mf, Scheduler scheduler, Duration timeout)
        implements MsgSender {

  @Override
  public Flux<Response> apply(Flux<Message> msgFlux) {
    return msgFlux.publishOn(scheduler).timeout(timeout).map(this::publish);
  }

  private Response publish(Message msg) {
    nc.publish(toSubject(mf.topic(), mf.partition()), msg.value().getBytes(StandardCharsets.UTF_8));
    return Response.empty();
  }
}
