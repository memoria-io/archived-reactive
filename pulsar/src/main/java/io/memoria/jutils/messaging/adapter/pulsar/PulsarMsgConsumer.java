package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.core.utils.functional.VavrUtils;
import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.messaging.domain.entity.Msg;
import io.memoria.jutils.messaging.domain.port.MsgConsumer;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.util.function.Function.identity;

public record PulsarMsgConsumer(PulsarClient client, Duration timeout) implements MsgConsumer {

  @Override
  public Flux<Try<Msg>> consume(String topicId, String partition, long offset) {
    return createConsumer(client, topicId, offset).map(PulsarMsgConsumer::consumeFrom)
                                                  .getOrElseGet(t -> Flux.just(Try.failure(t)))
                                                  .timeout(timeout);
  }

  @Override
  public Mono<Try<Void>> close() {
    return Mono.fromFuture(client.closeAsync().handle(VavrUtils.handle()));
  }

  private static Try<Consumer<String>> createConsumer(PulsarClient client, String topic, long offset) {
    return Try.of(() -> {
      var consumer = client.newConsumer(Schema.STRING)
                           .topic(topic)
                           .subscriptionName(topic + "subscription")
                           .subscribe();
      consumer.seek(offset);
      return consumer;
    });
  }

  private static Flux<Try<Msg>> consumeFrom(Consumer<String> c) {
    var f = Flux.<Flux<Try<Msg>>>generate(s -> s.next(receive(c))).flatMap(identity());
    return f.doFinally(s -> close(c));
  }

  private static Flux<Try<Msg>> receive(Consumer<String> c) {
    return Mono.fromFuture(c.receiveAsync().handle(VavrUtils.handle()))
               .map(m -> m.map(msg -> new Msg(msg.getKey(), msg.getValue())))
               .flux();
  }

  private static Mono<Void> close(Consumer<String> con) {
    return Mono.fromFuture(con.closeAsync());
  }
}
