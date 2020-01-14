package com.marmoush.jutils.adapter.msgbus.pulsar;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import com.marmoush.jutils.utils.functional.ReactorVavrUtils;
import com.marmoush.jutils.utils.functional.VavrUtils;
import io.vavr.Function1;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PulsarMsgConsumer implements MsgConsumer {
  private final Consumer<String> consumer;

  public PulsarMsgConsumer(Consumer<String> consumer) {
    this.consumer = consumer;
  }

  @Override
  public Flux<Try<ConsumerResp>> consume(String topicId, String partition, long offset) {
    var m = Mono.fromFuture(consumer.receiveAsync().handle(VavrUtils::cfHandle));
    //    m.flatMap(ReactorVavrUtils.tryToMonoTry())
    return null;
  }

  private static Function1<Message<String>, ConsumerResp> toSubResp(String topic, String partition) {
    //    return msg -> new ConsumerResp(new Msg(msg.getKey(), msg.getValue(), creationTime),
    //                                   msg.getTopicName(),
    //                                   partition,
    //                                   t)
    return null;
  }
}
