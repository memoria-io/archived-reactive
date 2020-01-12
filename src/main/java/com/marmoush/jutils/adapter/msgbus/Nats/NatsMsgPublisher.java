package com.marmoush.jutils.adapter.msgbus.Nats;

import com.marmoush.jutils.domain.port.msgbus.MsgPublisher;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.PublishResponse;
import io.nats.client.*;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class NatsMsgPublisher implements MsgPublisher {
  private final Connection nc;
  private final Scheduler scheduler;
  private final Duration timeout;

  public NatsMsgPublisher(Map<String, Object> configs, Scheduler scheduler, Duration timeout)
          throws IOException, InterruptedException {
    this.nc = NatsConnection.create(configs);
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
