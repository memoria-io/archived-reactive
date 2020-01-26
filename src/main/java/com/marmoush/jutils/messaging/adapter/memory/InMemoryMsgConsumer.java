package com.marmoush.jutils.messaging.adapter.memory;

import com.marmoush.jutils.messaging.domain.entity.Msg;
import com.marmoush.jutils.messaging.domain.port.MsgConsumer;
import io.vavr.control.Try;
import reactor.core.publisher.*;

import java.util.*;

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
