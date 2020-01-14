package com.marmoush.jutils.adapter.msgbus.pulsar;

import com.marmoush.jutils.domain.port.msgbus.MsgProducer;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.ProducerResp;
import com.marmoush.jutils.utils.functional.VavrUtils;
import io.vavr.Function1;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public class PulsarMsgProducer implements MsgProducer<MessageId> {
  public static final String MSG_ID_META_KEY = "message_id";
  private final Producer<String> producer;

  public PulsarMsgProducer(Producer<String> producer) {
    this.producer = producer;
  }

  @Override
  public Flux<Try<ProducerResp<MessageId>>> produce(String topic, String partitionStr, Flux<Msg> msgFlux) {
    return msgFlux.flatMap(toPulsarMessage(producer)).map(k -> k.map(id -> new ProducerResp<>(none(), some(id))));
  }

  private static Function1<Msg, Mono<Try<MessageId>>> toPulsarMessage(Producer<String> producer) {
    return msg -> Mono.fromFuture(producer.newMessage()
                                          .key(msg.key)
                                          .value(msg.value)
                                          .sendAsync()
                                          .handle(VavrUtils::cfHandle));
  }
}
