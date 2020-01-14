package com.marmoush.jutils.adapter.msgbus.Nats;

import com.marmoush.jutils.domain.port.msgbus.MsgSub;
import com.marmoush.jutils.domain.value.msg.SubResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Subscription;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

import static com.marmoush.jutils.adapter.msgbus.Nats.NatsConnection.CHANNEL_SEPARATOR;
import static io.vavr.control.Option.none;

public class NatsMsgSub implements MsgSub {
  private final Scheduler scheduler;
  private final Duration timeout;
  private final Connection nc;

  public NatsMsgSub(Connection nc, Scheduler scheduler, Duration timeout) {
    this.scheduler = scheduler;
    this.timeout = timeout;
    this.nc = nc;
  }

  @Override
  public Flux<Try<SubResp>> sub(String topic, String partition, long offset) {
    Subscription subscription = nc.subscribe(subject(topic, partition));
    var flux = Flux.<Try<SubResp>>generate(s -> s.next(pollOnce(subscription)));
    return Flux.defer(() -> flux.subscribeOn(scheduler));
  }

  private Try<SubResp> pollOnce(Subscription sub) {
    return Try.of(() -> sub.nextMessage(timeout)).map(NatsMsgSub::toConsumeResponse);
  }

  private static SubResp toConsumeResponse(Message m) {
    var msg = new Msg("", new String(m.getData()));
    var title = m.getSubject().split("\\" + CHANNEL_SEPARATOR);
    return new SubResp(msg, title[0], title[1], none(), none());
  }

  private static String subject(String topic, String partition) {
    return topic + CHANNEL_SEPARATOR + partition;
  }
}
