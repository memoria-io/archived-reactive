package io.memoria.jutils.adapter.messaging;

import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.messaging.MsgSender;
import io.memoria.jutils.core.messaging.Response;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public record InMemoryMsgSenderRepo(Map<String, HashMap<Integer, Queue<Message>>> db, MessageFilter mf)
        implements MsgSender {

  @Override
  public Mono<Response> apply(Message msg) {
    return Mono.fromCallable(() -> {
      db.putIfAbsent(mf.topic(), new HashMap<>());
      db.get(mf.topic()).putIfAbsent(mf.partition(), new LinkedList<>());
      db.get(mf.topic()).get(mf.partition()).add(msg);
      var i = db.get(mf.topic()).get(mf.partition()).size() - 1;
      return new Response(i);
    });
  }
}
