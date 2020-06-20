package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.messaging.domain.Message;
import io.memoria.jutils.messaging.domain.port.MsgReceiver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public record InMemoryMsgReceiver(Map<String, HashMap<String, LinkedList<Message>>>db) implements MsgReceiver {

  @Override
  public Flux<? extends Message> receive(String topicId, String partition, long offset) {
    return Flux.fromIterable(db.get(topicId).get(partition)).skip(offset);
  }

  @Override
  public Mono<Void> close() {
    db.clear();
    return Mono.empty();
  }
}
