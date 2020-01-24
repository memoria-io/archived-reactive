package com.marmoush.jutils.messaging.adapter.msgbus.pulsar;

import com.marmoush.jutils.messaging.domain.entity.Msg;
import com.marmoush.jutils.messaging.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.marmoush.jutils.utils.functional.VavrUtils.handle;
import static java.util.function.Function.identity;

public class PulsarMsgConsumer implements MsgConsumer {

  private final PulsarClient client;
  private final Duration timeout;

  public PulsarMsgConsumer(YamlConfigMap map) throws PulsarClientException {
    String url = map.asMap("pulsar").asString("serviceUrl");
    this.client = PulsarClient.builder().serviceUrl(url).build();
    this.timeout = Duration.ofMillis(map.asMap("reactorPulsar").asLong("request.timeout"));
  }

  @Override
  public Flux<Try<Msg>> consume(String topicId, String partition, long offset) {
    return createConsumer(client, topicId, offset).map(PulsarMsgConsumer::consumeFrom)
                                                  .getOrElseGet(t -> Flux.just(Try.failure(t)))
                                                  .timeout(timeout);
  }

  @Override
  public Mono<Try<Void>> close() {
    return Mono.fromFuture(client.closeAsync().handle(handle()));
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
    return Mono.fromFuture(c.receiveAsync().handle(handle()))
               .map(m -> m.map(msg -> new Msg(msg.getKey(), msg.getValue())))
               .flux();
  }

  private static Mono<Void> close(Consumer<String> con) {
    return Mono.fromFuture(con.closeAsync());
  }
}
