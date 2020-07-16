package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.MessageFilter;
import io.memoria.jutils.messaging.domain.Response;
import io.memoria.jutils.messaging.domain.port.MsgSender;
import reactor.core.publisher.Flux;

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
}
