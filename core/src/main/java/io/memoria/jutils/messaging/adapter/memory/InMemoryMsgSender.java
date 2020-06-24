package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import io.vavr.API;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public record InMemoryMsgSender(Map<String, HashMap<Integer, LinkedList<Message>>>db) implements MsgSender {

  @Override
  public Flux<Option<Message>> send(String topic, int partition, Flux<Message> msgFlux) {
    return msgFlux.doOnNext(msg -> {
      db.putIfAbsent(topic, new HashMap<>());
      db.get(topic).putIfAbsent(partition, new LinkedList<>());
      db.get(topic).get(partition).addLast(msg);
    }).map(API::Some);
  }
}
