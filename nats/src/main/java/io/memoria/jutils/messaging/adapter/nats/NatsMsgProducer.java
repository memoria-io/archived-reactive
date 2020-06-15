package io.memoria.jutils.messaging.adapter.nats;

import io.memoria.jutils.core.utils.functional.ReactorVavrUtils;
import io.memoria.jutils.messaging.domain.entity.Msg;
import io.memoria.jutils.messaging.domain.port.MsgProducer;
import io.nats.client.Connection;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static io.memoria.jutils.messaging.adapter.nats.NatsUtils.CHANNEL_SEPARATOR;

public record NatsMsgProducer(Connection nc, Scheduler scheduler, Duration timeout) implements MsgProducer {

  @Override
  public Flux<Try<Void>> produce(String topic, String partition, Flux<Msg> msgFlux) {
    return msgFlux.publishOn(scheduler)
                  .timeout(timeout)
                  .map(msg -> Try.run(() -> nc.publish(topic + CHANNEL_SEPARATOR + partition,
                                                       msg.value.getBytes(StandardCharsets.UTF_8))));
  }

  @Override
  public Mono<Try<Void>> close() {
    return ReactorVavrUtils.blockingToMono(() -> Try.run(nc::close), scheduler);
  }
}
