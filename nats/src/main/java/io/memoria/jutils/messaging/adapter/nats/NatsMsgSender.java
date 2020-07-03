package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.Response;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import io.nats.client.Connection;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

import static io.memoria.jutils.messaging.adapter.nats.NatsUtils.toSubject;

public class NatsMsgSender implements MsgSender {
  private final Connection nc;
  private final Scheduler scheduler;
  private final Duration timeout;

  public NatsMsgSender(Connection nc, Scheduler scheduler, Duration timeout) {
    this.nc = nc;
    this.scheduler = scheduler;
    this.timeout = timeout;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    NatsMsgSender that = (NatsMsgSender) o;
    return nc.equals(that.nc) && scheduler.equals(that.scheduler) && timeout.equals(that.timeout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nc, scheduler, timeout);
  }

  @Override
  public Flux<Response> send(String topic, int partition, Flux<Message> msgFlux) {
    return msgFlux.publishOn(scheduler).timeout(timeout).map(getMessageVoidFunction(topic, partition));
  }

  private Function<Message, Response> getMessageVoidFunction(String topic, int partition) {
    return msg -> {
      nc.publish(toSubject(topic, partition), msg.value().getBytes(StandardCharsets.UTF_8));
      return Response.empty();
    };
  }
}
