package com.marmoush.jutils.adapter.msgbus.Nats;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

import static com.marmoush.jutils.adapter.msgbus.Nats.NatsConnection.CHANNEL_SEPARATOR;
import static io.vavr.control.Option.none;

public class NatsMsgConsumer implements MsgConsumer<Void> {
  private final Scheduler scheduler;
  private final Duration timeout;
  private final Connection nc;

  public NatsMsgConsumer(Connection nc, Scheduler scheduler, Duration timeout) {
    this.scheduler = scheduler;
    this.timeout = timeout;
    this.nc = nc;
  }

  @Override
  public Flux<Try<ConsumerResp<Void>>> consume(String topic, String partition, long offset) {
    Subscription subscription = nc.subscribe(subject(topic, partition));
    var poll = Flux.<Try<ConsumerResp<Void>>>generate(s -> s.next(pollOnce(subscription)));
    return Flux.defer(() -> poll.subscribeOn(scheduler));
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
