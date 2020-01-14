package com.marmoush.jutils.adapter.msgbus.memory;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import com.marmoush.jutils.domain.value.msg.Msg;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryMsgConsumer implements MsgConsumer<Void> {
  private final Map<String, HashMap<String, LinkedList<Msg>>> db;

  public InMemoryMsgConsumer(Map<String, HashMap<String, LinkedList<Msg>>> db) {
    this.db = db;
  }

  @Override
  public Flux<Try<ConsumerResp<Void>>> consume(String topicId, String partition, long offset) {
    return Flux.fromIterable(db.get(topicId).get(partition)).map(msg -> Try.success(new ConsumerResp<>(msg)));
  }
}
