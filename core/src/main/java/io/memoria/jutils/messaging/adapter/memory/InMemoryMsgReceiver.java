package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public record InMemoryMsgReceiver(Map<String, HashMap<Integer, LinkedList<Message>>>db) implements MsgReceiver {

  @Override
  public Flux<Message> receive(String topicId, int partition, long offset) {
    return Flux.fromIterable(db.get(topicId).get(partition)).skip(offset);
  }
}
