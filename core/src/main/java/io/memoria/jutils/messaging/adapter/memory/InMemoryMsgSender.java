package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.Response;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public record InMemoryMsgSender(Map<String, HashMap<Integer, LinkedList<Message>>>db) implements MsgSender {

  @Override
  public Flux<Response> send(String topic, int partition, Flux<Message> msgFlux) {
    return msgFlux.map(msg -> {
      db.putIfAbsent(topic, new HashMap<>());
      db.get(topic).putIfAbsent(partition, new LinkedList<>());
      db.get(topic).get(partition).addLast(msg);
      return db.get(topic).get(partition).size() - 1;
    }).map(Response::new);
  }
}
