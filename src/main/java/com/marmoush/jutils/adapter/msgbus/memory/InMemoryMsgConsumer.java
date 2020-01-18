package com.marmoush.jutils.adapter.msgbus.memory;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryMsgConsumer implements MsgConsumer<Integer> {
  private final Map<String, HashMap<String, LinkedList<Msg>>> db;

  public InMemoryMsgConsumer(Map<String, HashMap<String, LinkedList<Msg>>> db) {
    this.db = db;
  }

  @Override
  public Flux<Try<ConsumerResp<Integer>>> consume(String topicId, String partition, long offset) {
    return Flux.fromIterable(db.get(topicId).get(partition))
               .skip(offset)
               .map(msg -> Try.success(new ConsumerResp<>(msg, LocalDateTime.now(), Option.some(1))));
  }

  @Override
  public Mono<Try<Void>> close() {
    return Mono.just(Try.run(db::clear));
  }
}
