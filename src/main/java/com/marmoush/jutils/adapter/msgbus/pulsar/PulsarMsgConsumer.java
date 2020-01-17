package com.marmoush.jutils.adapter.msgbus.pulsar;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import io.vavr.control.Try;
import org.apache.pulsar.client.api.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.marmoush.jutils.utils.functional.VavrUtils.handle;
import static io.vavr.control.Option.some;
import static java.util.function.Function.identity;

public class PulsarMsgConsumer implements MsgConsumer<Message<String>> {

  private final PulsarClient client;
  private final Duration timeout;

  public PulsarMsgConsumer(YamlConfigMap map) throws PulsarClientException {
    String url = map.asMap("pulsar").asString("serviceUrl");
    this.client = PulsarClient.builder().serviceUrl(url).build();
    this.timeout = Duration.ofMillis(map.asMap("reactorPulsar").asLong("request.timeout"));
  }

  // TODO Receive async
  @Override
  public Flux<Try<ConsumerResp<Message<String>>>> consume(String topicId, String partition, long offset) {
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

  private static Flux<Try<ConsumerResp<Message<String>>>> consumeFrom(Consumer<String> c) {
    var f = Flux.<Flux<Try<Message<String>>>>generate(s -> s.next(receive(c).flux())).flatMap(identity());
    return f.map(t -> t.map(PulsarMsgConsumer::toConsumerResp)).doFinally(s -> close(c));
  }

  private static Mono<Try<Message<String>>> receive(Consumer<String> c) {
    return Mono.fromFuture(c.receiveAsync().handle(handle()));
  }

  private static ConsumerResp<Message<String>> toConsumerResp(Message<String> msg) {
    return new ConsumerResp<>(new Msg(msg.getValue(), some(msg.getKey())), LocalDateTime.now(), some(msg));
  }

  private static Mono<Void> close(Consumer<String> con) {
    return Mono.fromFuture(con.closeAsync());
  }
}
