package io.memoria.jutils.messaging.adapter.pulsar;

import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.Response;
import io.memoria.jutils.core.messaging.MsgSender;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.impl.MessageIdImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.vavr.control.Option.some;

public record PulsarSender(Producer<String>producer) implements MsgSender {

  @Override
  public Flux<Response> apply(Flux<Message> msgFlux) {
    return msgFlux.concatMap(this::send).map(this::toResponse);
  }

  private Mono<MessageId> send(Message message) {
    var pm = producer.newMessage();
    if (message.id().isDefined()) {
      pm = pm.sequenceId(message.id().get());
    }
    return Mono.fromFuture(pm.value(message.value()).sendAsync());
  }

  private Response toResponse(MessageId id) {
    var entry = ((MessageIdImpl) id).getEntryId();
    var ledger = String.valueOf(((MessageIdImpl) id).getLedgerId());
    var partition = String.valueOf(((MessageIdImpl) id).getPartitionIndex());
    return new Response(some(entry), Option.none(), HashMap.of("ledgerId", ledger, "partition", partition));
  }
}
