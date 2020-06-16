package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.core.utils.functional.VavrUtils;
import io.memoria.jutils.messaging.domain.entity.Msg;
import io.memoria.jutils.messaging.domain.port.MsgProducer;
import io.vavr.Function1;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Function;

public record PulsarMsgProducer(PulsarClient client, Duration timeout) implements MsgProducer {


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
    return Mono.fromFuture(client.closeAsync().handle(VavrUtils.handle()));
  }

  private static Try<Producer<String>> createProducer(PulsarClient client, String topic) {
    return Try.of(() -> client.newProducer(Schema.STRING).topic(topic).create());
  }

  private static Function1<Msg, Mono<Try<Void>>> send(Producer<String> producer, String partition) {
    return msg -> Mono.fromFuture(producer.newMessage()
                                          .key(partition)
                                          .value(msg.value())
                                          .sendAsync()
                                          .handle(VavrUtils.handleToVoid()));
  }

  private static Mono<Void> close(Producer<String> prod) {
    return Mono.fromFuture(prod.flushAsync()).then(Mono.fromFuture(prod.closeAsync()));
  }
}
