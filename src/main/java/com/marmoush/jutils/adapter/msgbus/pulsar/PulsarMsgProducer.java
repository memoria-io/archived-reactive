package com.marmoush.jutils.adapter.msgbus.pulsar;

import com.marmoush.jutils.domain.port.msgbus.MsgProducer;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.ProducerResp;
import io.vavr.Function1;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.marmoush.jutils.utils.functional.VavrUtils.handle;
import static com.marmoush.jutils.utils.functional.VavrUtils.handleWithTry;
import static io.vavr.control.Option.some;

public class PulsarMsgProducer implements MsgProducer<MessageId> {
  private final Producer<String> producer;

  public PulsarMsgProducer(Producer<String> producer) {
    this.producer = producer;
  }

  @Override
  public Flux<Try<ProducerResp<MessageId>>> produce(String topic, String partitionStr, Flux<Msg> msgFlux) {
    return msgFlux.flatMap(toPulsarMessage(producer)).map(k -> k.map(id -> new ProducerResp<>(some(id))));
  }

  private static Function1<Msg, Mono<Try<MessageId>>> toPulsarMessage(Producer<String> producer) {
    return msg -> Mono.fromFuture(toPulsarMessage(producer, msg).sendAsync().handle(handle()));
  }

  private static TypedMessageBuilder<String> toPulsarMessage(Producer<String> producer, Msg msg) {
    var m = producer.newMessage().value(msg.value);
    return (msg.pkey.isDefined()) ? m.key(msg.pkey.get()) : m;
  }
}
