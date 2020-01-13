package com.marmoush.jutils.adapter.msgbus.Nats;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumeResponse;
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

public class NatsMsgConsumer implements MsgConsumer {
  private final Scheduler scheduler;
  private final Duration timeout;
  private final Connection nc;

  public NatsMsgConsumer(Connection nc, Scheduler scheduler, Duration timeout) {
    this.scheduler = scheduler;
    this.timeout = timeout;
    this.nc = nc;
  }

  @Override
  public Flux<Try<ConsumeResponse>> consume(String topic, String partition, long offset) {
    Subscription subscription = nc.subscribe(topic);
    var flux = Flux.<Try<ConsumeResponse>>generate(s -> s.next(pollOnce(subscription)));
    return Flux.defer(() -> flux.subscribeOn(scheduler));
  }

  private Try<ConsumeResponse> pollOnce(Subscription sub) {
    return Try.of(() -> sub.nextMessage(timeout)).map(NatsMsgConsumer::toConsumeResponse);
  }

  private static ConsumeResponse toConsumeResponse(Message m) {
    var msg = new Msg("", new String(m.getData()));
    var title = m.getSubject().split("\\" + CHANNEL_SEPARATOR);
    return new ConsumeResponse(msg, title[0], title[1], none(), none());
  }
}
