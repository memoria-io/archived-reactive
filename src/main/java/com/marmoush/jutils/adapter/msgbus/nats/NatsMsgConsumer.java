package com.marmoush.jutils.adapter.msgbus.nats;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.time.Duration;

import static com.marmoush.jutils.adapter.msgbus.nats.NatsConnection.CHANNEL_SEPARATOR;
import static com.marmoush.jutils.adapter.msgbus.nats.NatsConnection.create;
import static com.marmoush.jutils.utils.functional.ReactorVavrUtils.blockingToMono;
import static io.vavr.control.Option.none;

public class NatsMsgConsumer implements MsgConsumer<Void> {
  private final Scheduler scheduler;
  private final Duration timeout;
  private final Connection nc;

  public NatsMsgConsumer(YamlConfigMap map, Scheduler scheduler) throws IOException, InterruptedException {
    this.scheduler = scheduler;
    this.timeout = Duration.ofMillis(map.asMap("reactorNats").asLong("consumer.request.timeout"));
    this.nc = create(map);
  }

  @Override
  public Flux<Try<ConsumerResp<Void>>> consume(String topic, String partition, long offset) {
    Subscription subscription = nc.subscribe(subject(topic, partition));
    var poll = Flux.<Try<ConsumerResp<Void>>>generate(s -> s.next(pollOnce(subscription)));
    return Flux.defer(() -> poll.subscribeOn(scheduler));
  }

  @Override
  public Mono<Try<Void>> close() {
    return blockingToMono(() -> Try.run(() -> nc.close()), scheduler);
  }

  private Try<ConsumerResp<Void>> pollOnce(Subscription sub) {
    return Try.of(() -> sub.nextMessage(timeout)).map(NatsMsgConsumer::toConsumeResponse);
  }

  private static ConsumerResp<Void> toConsumeResponse(Message m) {
    return new ConsumerResp<>(new Msg(new String(m.getData()), none()));
  }

  private static String subject(String topic, String partition) {
    return topic + CHANNEL_SEPARATOR + partition;
  }
}
