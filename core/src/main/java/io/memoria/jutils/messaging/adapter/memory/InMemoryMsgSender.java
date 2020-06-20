package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import io.vavr.API;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public record InMemoryMsgSender(Map<String, HashMap<String, LinkedList<Message>>>db) implements MsgSender {

  @Override
  public Flux<Option<? extends Message>> send(String topic, String partition, Flux<? extends Message> msgFlux) {
    return msgFlux.doOnNext(msg -> {
      db.putIfAbsent(topic, new HashMap<>());
      db.get(topic).putIfAbsent(partition, new LinkedList<>());
      db.get(topic).get(partition).addLast(msg);
    }).map(API::Some);
  }

  @Override
  public Mono<Void> close() {
    db.clear();
    return Mono.empty();
  }
}
