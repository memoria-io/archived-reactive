package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.messaging.domain.entity.Msg;
import io.memoria.jutils.messaging.domain.port.MsgProducer;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public record InMemoryMsgProducer(Map<String, HashMap<String, LinkedList<Msg>>>db) implements MsgProducer {

  @Override
  public Flux<Try<Void>> produce(String topic, String partition, Flux<Msg> msgFlux) {
    return msgFlux.map(msg -> {
      db.putIfAbsent(topic, new HashMap<>());
      db.get(topic).putIfAbsent(partition, new LinkedList<>());
      db.get(topic).get(partition).addLast(msg);
      return Try.success(null);
    });
  }

  @Override
  public Mono<Try<Void>> close() {
    return Mono.just(Try.run(db::clear));
  }
}
