package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import org.apache.pulsar.client.api.Consumer;
import reactor.core.publisher.Flux;

import static io.memoria.jutils.messaging.adapter.pulsar.PulsarUtils.consume;
import static java.util.function.Function.identity;

public record PulsarReceiver(Consumer<String>consumer) implements MsgReceiver {

  @Override
  public Flux<Message> get() {
    return Flux.<Flux<Message>>generate(s -> s.next(consume(consumer).flux())).flatMap(identity());
  }
}
