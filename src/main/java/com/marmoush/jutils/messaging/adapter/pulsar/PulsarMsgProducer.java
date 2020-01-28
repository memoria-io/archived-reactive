package com.marmoush.jutils.messaging.adapter.pulsar;

import com.marmoush.jutils.messaging.domain.entity.Msg;
import com.marmoush.jutils.messaging.domain.port.MsgProducer;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import io.vavr.Function1;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.*;
import reactor.core.publisher.*;

import java.time.Duration;
import java.util.function.Function;

import static com.marmoush.jutils.utils.functional.VavrUtils.*;

public class PulsarMsgProducer implements MsgProducer {

  private final PulsarClient client;
  private final Duration timeout;

  public PulsarMsgProducer(YamlConfigMap map) throws PulsarClientException {
    String url = map.asMap("pulsar").asString("serviceUrl");
    this.client = PulsarClient.builder().serviceUrl(url).build();
    this.timeout = Duration.ofMillis(map.asMap("reactorPulsar").asLong("request.timeout"));
  }

  @Override
  public Flux<Try<Void>> produce(String topic, String partitionStr, Flux<Msg> msgFlux) {
    return createProducer(client, topic).map(produceFrom(msgFlux, partitionStr))
                                        .getOrElseGet(f -> Flux.just(Try.failure(f)))
                                        .timeout(timeout);
  }

  private Function<Producer<String>, Flux<Try<Void>>> produceFrom(Flux<Msg> msgFlux, String partition) {
    return prod -> msgFlux.flatMap(send(prod, partition)).doFinally(s -> close(prod).subscribe());
  }

  @Override
  public Mono<Try<Void>> close() {
    return Mono.fromFuture(client.closeAsync().handle(handle()));
  }

  private static Try<Producer<String>> createProducer(PulsarClient client, String topic) {
    return Try.of(() -> client.newProducer(Schema.STRING).topic(topic).create());
  }

  private static Function1<Msg, Mono<Try<Void>>> send(Producer<String> producer, String partition) {
    return msg -> Mono.fromFuture(producer.newMessage()
                                          .key(partition)
                                          .value(msg.value)
                                          .sendAsync()
                                          .handle(handleToVoid()));
  }

  private static Mono<Void> close(Producer<String> prod) {
    return Mono.fromFuture(prod.flushAsync()).then(Mono.fromFuture(prod.closeAsync()));
  }
}
