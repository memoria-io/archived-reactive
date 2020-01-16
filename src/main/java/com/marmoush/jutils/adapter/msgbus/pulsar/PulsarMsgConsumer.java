package com.marmoush.jutils.adapter.msgbus.pulsar;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.marmoush.jutils.utils.functional.VavrUtils.handle;
import static io.vavr.control.Option.some;

public class PulsarMsgConsumer implements MsgConsumer<Message<String>> {
  private static final Logger log = LoggerFactory.getLogger(PulsarMsgConsumer.class.getName());

  private final PulsarClient client;

  public PulsarMsgConsumer(YamlConfigMap map) throws PulsarClientException {
    String url = map.asString("pulsar.serviceUrl");
    this.client = PulsarClient.builder().serviceUrl(url).build();
  }

  //TODO fix error
  @Override
  public Flux<Try<ConsumerResp<Message<String>>>> consume(String topicId, String partition, long offset) {
    return createConsumer(client, topicId, offset).map(consumer -> {
      var consume = Mono.fromFuture(consumer.receiveAsync().handle(handle()))
                        .map(t -> t.map(PulsarMsgConsumer::toSubResp));
      return Flux.<Mono<Try<ConsumerResp<Message<String>>>>>generate(s -> s.next(consume)).flatMap(Flux::from)
                                                                                          .doFinally(s -> close(consumer)
                                                                                                  .subscribe());
    }).getOrElseGet(t -> Flux.just(Try.failure(t)));
  }

  public Mono<Void> close() {
    return Mono.fromFuture(client.closeAsync());
  }

  private static Try<Consumer<String>> createConsumer(PulsarClient client, String topic, long offset) {
    return Try.of(() -> {
      var consumer = client.newConsumer(Schema.STRING)
                           .topic(topic)
                           .subscriptionName(topic + "subscription")
                           .subscribe();
      consumer.seek(MessageId.earliest);
      return consumer;
    });
  }

  private static ConsumerResp<Message<String>> toSubResp(Message<String> msg) {
    return new ConsumerResp<>(new Msg(msg.getValue(), some(msg.getKey())), LocalDateTime.now(), some(msg));
  }

  private static Mono<Void> close(Consumer<String> con) {
    return Mono.fromFuture(con.closeAsync());
  }
}
