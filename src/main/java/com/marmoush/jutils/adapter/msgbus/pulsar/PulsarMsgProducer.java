package com.marmoush.jutils.adapter.msgbus.pulsar;

import com.marmoush.jutils.domain.port.msgbus.MsgProducer;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.ProducerResp;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import io.vavr.Function1;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.marmoush.jutils.utils.functional.VavrUtils.handle;
import static io.vavr.control.Option.some;

public class PulsarMsgProducer implements MsgProducer<MessageId> {

  private final PulsarClient client;
  private final Duration timeout;

  public PulsarMsgProducer(YamlConfigMap map) throws PulsarClientException {
    String url = map.asMap("pulsar").asString("serviceUrl");
    this.client = PulsarClient.builder().serviceUrl(url).build();
    this.timeout = Duration.ofMillis(map.asMap("reactorPulsar").asLong("request.timeout"));
  }

  @Override
  public Flux<Try<ProducerResp<MessageId>>> produce(String topic, String partitionStr, Flux<Msg> msgFlux) {
    return createProducer(client, topic).map(prod -> msgFlux.flatMap(toPulsarMessage(prod))
                                                            .map(k -> k.map(id -> new ProducerResp<>(some(id))))
                                                            .doFinally(s -> closeProducer(prod).subscribe()))
                                        .getOrElseGet(f -> Flux.just(Try.failure(f)));
  }

  @Override
  public Mono<Try<Void>> close() {
    return Mono.fromFuture(client.closeAsync().handle(handle()));
  }

  private static Function1<Msg, Mono<Try<MessageId>>> toPulsarMessage(Producer<String> producer) {
    return msg -> Mono.fromFuture(toPulsarMessage(producer, msg).sendAsync().handle(handle()));
  }

  private static TypedMessageBuilder<String> toPulsarMessage(Producer<String> producer, Msg msg) {
    return msg.pkey.map(key -> producer.newMessage().key(key).value(msg.value))
                   .getOrElse(producer.newMessage().value(msg.value));
  }

  private static Try<Producer<String>> createProducer(PulsarClient client, String topic) {
    return Try.of(() -> client.newProducer(Schema.STRING).topic(topic).create());
  }

  private static Mono<Void> closeProducer(Producer<String> prod) {
    return Mono.fromFuture(prod.flushAsync()).then(Mono.fromFuture(prod.closeAsync()));
  }
}
