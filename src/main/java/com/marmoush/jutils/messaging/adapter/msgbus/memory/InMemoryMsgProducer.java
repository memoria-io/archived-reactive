package com.marmoush.jutils.messaging.adapter.msgbus.memory;

import com.marmoush.jutils.general.domain.entity.Msg;
import com.marmoush.jutils.messaging.domain.port.msgbus.MsgProducer;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryMsgProducer implements MsgProducer {
  private final Map<String, HashMap<String, LinkedList<Msg>>> db;

  public InMemoryMsgProducer(Map<String, HashMap<String, LinkedList<Msg>>> db) {
    this.db = db;
  }

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
