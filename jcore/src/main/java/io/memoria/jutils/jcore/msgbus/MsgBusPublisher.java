package io.memoria.jutils.jcore.msgbus;

import reactor.core.publisher.Mono;

public interface MsgBusPublisher {
  Mono<Void> beginTransaction();

  Mono<Void> abortTransaction();

  Mono<Void> commitTransaction();

  Mono<Long> publish(String msg);
}
