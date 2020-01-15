package com.marmoush.jutils.adapter.msgbus.Nats;

import com.marmoush.jutils.domain.port.msgbus.MsgProducer;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.ProducerResp;
import io.nats.client.Connection;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.marmoush.jutils.adapter.msgbus.Nats.NatsConnection.CHANNEL_SEPARATOR;

public class NatsMsgProducer implements MsgProducer<Void> {
  private final Connection nc;
  private final Scheduler scheduler;
  private final Duration timeout;

  public NatsMsgProducer(Connection nc, Scheduler scheduler, Duration timeout) {
    this.nc = nc;
    this.scheduler = scheduler;
    this.timeout = timeout;
  }

  @Override
  public Flux<Try<ProducerResp<Void>>> produce(String topic, String partition, Flux<Msg> msgFlux) {
    return msgFlux.publishOn(scheduler).map(msg -> Try.of(() -> {
      nc.publish(topic + CHANNEL_SEPARATOR + partition, msg.value.getBytes(StandardCharsets.UTF_8));
      return new ProducerResp<Void>();
    })).timeout(timeout);
  }
}