package io.memoria.jutils.core.messaging;

import io.vavr.Function1;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface MsgSender extends Function1<Message, Mono<Response>> {
  default Flux<Response> apply(Flux<Message> messages) {
    return messages.concatMap(this);
  }
}
