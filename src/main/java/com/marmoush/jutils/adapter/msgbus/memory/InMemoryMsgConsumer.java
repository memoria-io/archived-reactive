package com.marmoush.jutils.adapter.msgbus.memory;

import com.marmoush.jutils.domain.entity.Msg;
import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryMsgConsumer implements MsgConsumer {
  private final Map<String, HashMap<String, LinkedList<Msg>>> db;

  public InMemoryMsgConsumer(Map<String, HashMap<String, LinkedList<Msg>>> db) {
    this.db = db;
  }

  @Override
  public Flux<Try<Msg>> consume(String topicId, String partition, long offset) {
    return Flux.fromIterable(db.get(topicId).get(partition)).skip(offset).map(Try::success);
  }

  @Override
  public Mono<Try<Void>> close() {
    return Mono.just(Try.run(db::clear));
  }
}
