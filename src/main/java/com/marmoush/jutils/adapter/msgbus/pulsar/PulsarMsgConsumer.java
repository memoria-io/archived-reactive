package com.marmoush.jutils.adapter.msgbus.pulsar;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.utils.functional.VavrUtils;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.marmoush.jutils.utils.functional.VavrUtils.toTry;
import static io.vavr.control.Option.some;

public class PulsarMsgConsumer implements MsgConsumer<Message<String>> {
  private final Consumer<String> consumer;

  public PulsarMsgConsumer(Consumer<String> consumer) {
    this.consumer = consumer;
  }

  @Override
  public Flux<Try<ConsumerResp<Message<String>>>> consume(String topicId, String partition, long offset) {
    var consume = Mono.fromFuture(toTry(consumer.receiveAsync())).map(t -> t.map(PulsarMsgConsumer::toSubResp));
    return Flux.<Mono<Try<ConsumerResp<Message<String>>>>>generate(s -> s.next(consume)).flatMap(Flux::from);
  }

  private static ConsumerResp<Message<String>> toSubResp(Message<String> msg) {
    return new ConsumerResp<>(new Msg(msg.getValue(), some(msg.getKey())));
  }
}
