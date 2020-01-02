package com.marmoush.jutils.adapter.msgbus.memory;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.value.msg.ConsumeResponse;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryMsgConsumer implements MsgConsumer {
  private final Map<String, HashMap<Integer, LinkedList<ConsumeResponse>>> db;
  private final int partitions;

  public InMemoryMsgConsumer(Map<String, HashMap<Integer, LinkedList<ConsumeResponse>>> db, int partitions) {
    this.db = db;
    this.partitions = partitions;
  }

  @Override
  public Flux<Try<ConsumeResponse>> consume(String topicId, int partition, long offset) {
    return Flux.fromIterable(db.get(topicId).get(partition)).map(Try::success);
  }
}
