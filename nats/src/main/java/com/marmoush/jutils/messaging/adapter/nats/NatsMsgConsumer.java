package com.marmoush.jutils.messaging.adapter.nats;

import com.marmoush.jutils.core.utils.yaml.YamlConfigMap;
import com.marmoush.jutils.messaging.domain.entity.Msg;
import com.marmoush.jutils.messaging.domain.port.MsgConsumer;
import io.nats.client.Connection;
import io.nats.client.Subscription;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.time.Duration;

import static com.marmoush.jutils.core.utils.functional.ReactorVavrUtils.blockingToMono;

public class NatsMsgConsumer implements MsgConsumer {
  private final Scheduler scheduler;
  private final Duration timeout;
  private final Connection nc;

  public NatsMsgConsumer(YamlConfigMap map, Scheduler scheduler) throws IOException, InterruptedException {
    this.scheduler = scheduler;
    this.timeout = Duration.ofMillis(map.asYamlConfigMap("reactorNats").asLong("consumer.request.timeout"));
    this.nc = NatsConnection.create(map);
  }

  /**
   * Topic = topic + CHANNEL_SEPARATOR + partition
   */
  @Override
  public Flux<Try<Msg>> consume(String topic, String partition, long offset) {
    Subscription subscription = nc.subscribe(topic + NatsConnection.CHANNEL_SEPARATOR + partition);
    var poll = Flux.<Try<Msg>>generate(s -> s.next(pollOnce(subscription)));
    return Flux.defer(() -> poll.subscribeOn(scheduler).skip(offset));
  }

  @Override
  public Mono<Try<Void>> close() {
    return blockingToMono(() -> Try.run(nc::close), scheduler);
  }

  private Try<Msg> pollOnce(Subscription sub) {
    return Try.of(() -> sub.nextMessage(timeout)).map(m -> new Msg(m.getSID(), new String(m.getData())));
  }
}
