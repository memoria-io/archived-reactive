package com.marmoush.jutils.adapter.msgbus.Nats;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumeResponse;
import com.marmoush.jutils.domain.value.msg.Msg;
import io.nats.client.Connection;
import io.nats.client.Subscription;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Consumer;

import static com.marmoush.jutils.utils.functional.VavrUtils.traversableT;
import static io.vavr.control.Option.none;

public class NatsMsgConsumer implements MsgConsumer {
  private final Scheduler scheduler;
  private final Duration timeout;
  private final Connection nc;

  public NatsMsgConsumer(Map<String, Object> configs, Scheduler scheduler, Duration timeout)
          throws IOException, InterruptedException {
    this.scheduler = scheduler;
    this.timeout = timeout;
    this.nc = NatsConnection.create(configs);
  }

  @Override
  public Flux<Try<ConsumeResponse>> consume(String topic, String partition, long offset) {
    var consumer = Mono.just(nc.subscribe(topic)).flatMapMany(m -> {
      Consumer<SynchronousSink<Try<ConsumeResponse>>> poll = s -> s.next(pollOnce(m, topic, partition));
      return Flux.generate(poll);
    });
    return Flux.defer(() -> consumer.subscribeOn(scheduler));
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
