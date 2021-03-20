package io.memoria.jutils.jcore.msgbus;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface MsgBusSubscriber {
  Flux<String> subscribe();
}
