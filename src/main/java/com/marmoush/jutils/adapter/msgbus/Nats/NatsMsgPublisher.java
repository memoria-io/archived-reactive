package com.marmoush.jutils.adapter.msgbus.Nats;

import com.marmoush.jutils.domain.port.msgbus.MsgPublisher;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.PublishResponse;
import io.nats.client.Connection;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class NatsMsgPublisher implements MsgPublisher {
  private final Connection nc;
  private final Scheduler scheduler;
  private final Duration timeout;

  public NatsMsgPublisher(Connection nc, Scheduler scheduler, Duration timeout) {
    this.nc = nc;
    this.scheduler = scheduler;
    this.timeout = timeout;
  }

  @Override
  public Flux<Try<PublishResponse>> publish(Flux<Msg> msgFlux, String topic, String partition) {
    return msgFlux.publishOn(scheduler).map(msg -> Try.of(() -> {
      nc.publish(topic, msg.value.getBytes(StandardCharsets.UTF_8));
      return new PublishResponse(topic, partition);
    })).timeout(timeout);
  }
}
