package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.MessageFilter;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public record MemoryMsgReceiver(Map<String, HashMap<Integer, LinkedList<Message>>>db, MessageFilter mf)
        implements MsgReceiver {

  @Override
  public Flux<Message> get() {
    return Flux.fromIterable(db.get(mf.topic()).get(mf.partition())).skip(mf.offset());
  }
}
