package io.memoria.jutils.core.messaging;

import io.memoria.jutils.core.messaging.Message;
import io.memoria.jutils.core.messaging.MessageFilter;
import io.memoria.jutils.core.messaging.MsgReceiver;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public record InMemoryMsgReceiverRepo(Map<String, HashMap<Integer, Queue<Message>>> db, MessageFilter mf)
        implements MsgReceiver {

  @Override
  public Flux<Message> get() {
    return Mono.fromCallable(() -> {
      var t = Try.of(() -> db.get(mf.topic()).get(mf.partition()));
      return t.getOrElse(new LinkedList<>());
    }).flatMapMany(Flux::fromIterable).skip(mf.offset());
  }
}
