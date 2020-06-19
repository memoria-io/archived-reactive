package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.messaging.domain.entity.Msg;
import io.memoria.jutils.messaging.domain.entity.Response;
import io.memoria.jutils.messaging.domain.port.MsgProducer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public record InMemoryMsgProducer(Map<String, HashMap<String, LinkedList<Msg>>>db) implements MsgProducer {

  @Override
  public Flux<Response> produce(String topic, String partition, Flux<Msg> msgFlux) {
    return msgFlux.doOnNext(msg -> {
      db.putIfAbsent(topic, new HashMap<>());
      db.get(topic).putIfAbsent(partition, new LinkedList<>());
      db.get(topic).get(partition).addLast(msg);
    }).map(m -> m::id);
  }

  @Override
  public Mono<Void> close() {
    db.clear();
    return Mono.empty();
  }
}
