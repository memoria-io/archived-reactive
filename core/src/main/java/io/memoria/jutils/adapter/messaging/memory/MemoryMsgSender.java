package io.memoria.jutils.adapter.messaging.memory;

import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.messaging.MsgSender;
import io.memoria.jutils.core.messaging.Response;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public record MemoryMsgSender(Map<String, HashMap<Integer, LinkedList<Message>>>db, MessageFilter mf)
        implements MsgSender {

  @Override
  public Flux<Response> apply(Flux<Message> msgFlux) {
    return msgFlux.map(msg -> {
      db.putIfAbsent(mf.topic(), new HashMap<>());
      db.get(mf.topic()).putIfAbsent(mf.partition(), new LinkedList<>());
      db.get(mf.topic()).get(mf.partition()).addLast(msg);
      return db.get(mf.topic()).get(mf.partition()).size() - 1;
    }).map(Response::new);
  }

  @Override
  public Mono<Response> apply(Message message) {
    return null;
  }
}
