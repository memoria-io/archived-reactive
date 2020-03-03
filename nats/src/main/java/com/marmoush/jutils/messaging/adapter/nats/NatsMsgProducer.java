package com.marmoush.jutils.messaging.adapter.nats;

import com.marmoush.jutils.core.utils.yaml.YamlConfigMap;
import com.marmoush.jutils.messaging.domain.entity.Msg;
import com.marmoush.jutils.messaging.domain.port.MsgProducer;
import io.nats.client.Connection;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.marmoush.jutils.core.utils.functional.ReactorVavrUtils.blockingToMono;
import static com.marmoush.jutils.messaging.adapter.nats.NatsConnection.CHANNEL_SEPARATOR;
import static com.marmoush.jutils.messaging.adapter.nats.NatsConnection.create;

public class NatsMsgProducer implements MsgProducer {
  private final Connection nc;
  private final Scheduler scheduler;
  private final Duration timeout;

  public NatsMsgProducer(YamlConfigMap map, Scheduler scheduler) throws IOException, InterruptedException {
    this.scheduler = scheduler;
    this.timeout = Duration.ofMillis(map.asYamlConfigMap("reactorNats").asLong("producer.request.timeout"));
    this.nc = create(map);
  }

  @Override
  public Flux<Try<Void>> produce(String topic, String partition, Flux<Msg> msgFlux) {
    return msgFlux.publishOn(scheduler)
                  .timeout(timeout)
                  .map(msg -> Try.run(() -> nc.publish(topic + CHANNEL_SEPARATOR + partition,
                                                       msg.value.getBytes(StandardCharsets.UTF_8))));
  }

  @Override
  public Mono<Try<Void>> close() {
    return blockingToMono(() -> Try.run(nc::close), scheduler);
  }
}
