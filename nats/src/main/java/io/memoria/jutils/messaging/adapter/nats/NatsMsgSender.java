package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import io.nats.client.Connection;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Function;

import static io.memoria.jutils.messaging.adapter.nats.NatsUtils.toSubject;

public record NatsMsgSender(Connection nc, Scheduler scheduler, Duration timeout) implements MsgSender {

  private Function<Message, Option<Message>> getMessageVoidFunction(String topic, String partition) {
    return msg -> {
      nc.publish(toSubject(topic, partition), msg.message().getBytes(StandardCharsets.UTF_8));
      return Option.none();
    };
  }

  @Override
  public Flux<Option<Message>> send(String topic, String partition, Flux<Message> msgFlux) {
    return msgFlux.publishOn(scheduler).timeout(timeout).map(getMessageVoidFunction(topic, partition));
  }
}
