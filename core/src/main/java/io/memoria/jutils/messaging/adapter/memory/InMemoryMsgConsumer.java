package io.memoria.jutils.messaging.adapter.memory;

import io.memoria.jutils.messaging.domain.entity.Msg;
import io.memoria.jutils.messaging.domain.port.MsgConsumer;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public record InMemoryMsgConsumer(Map<String, HashMap<String, LinkedList<Msg>>>db) implements MsgConsumer {

  @Override
  public Flux<Try<Msg>> consume(String topicId, String partition, long offset) {
    return Flux.fromIterable(db.get(topicId).get(partition)).skip(offset).map(Try::success);
  }

  @Override
  public Mono<Try<Void>> close() {
    return Mono.just(Try.run(db::clear));
  }
}
