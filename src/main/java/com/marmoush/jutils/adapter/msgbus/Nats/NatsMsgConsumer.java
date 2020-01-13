package com.marmoush.jutils.adapter.msgbus.Nats;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumeResponse;
import com.marmoush.jutils.domain.value.msg.Msg;
import io.nats.client.Connection;
import io.nats.client.Subscription;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Consumer;

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
    var flux = Flux.<Try<ConsumeResponse>>generate(s -> s.next(pollOnce(subscription, topic, partition)));
    return Flux.defer(() -> flux.subscribeOn(scheduler));
  }

  private Try<ConsumeResponse> pollOnce(Subscription sub, String topicId, String partition) {
    return Try.of(() -> sub.nextMessage(timeout))
              .map(msg -> new ConsumeResponse(new Msg("", new String(msg.getData())),
                                              topicId,
                                              partition,
                                              none(),
                                              none()));
  }
}
