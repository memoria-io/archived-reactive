package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import io.vavr.control.Option;
import org.apache.pulsar.client.api.Consumer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public record PulsarReceiver(Consumer<String>consumer, Duration frequency) implements MsgReceiver {

  @Override
  public Flux<Message> get() {
    return Flux.interval(frequency).concatMap(i -> consume());
  }

  private Mono<Message> consume() {
    return Mono.fromFuture(consumer.receiveAsync()).map(this::toMessage);
  }

  private Message toMessage(org.apache.pulsar.client.api.Message<String> m) {
    Option<Long> id = (m.getSequenceId() != -1) ? some(m.getSequenceId()) : none();
    return new Message(m.getValue(), id);
  }
}
