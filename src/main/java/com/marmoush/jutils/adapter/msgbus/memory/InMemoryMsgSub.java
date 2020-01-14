package com.marmoush.jutils.adapter.msgbus.memory;

import com.marmoush.jutils.domain.port.msgbus.MsgSub;
import com.marmoush.jutils.domain.value.msg.SubResp;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryMsgSub implements MsgSub {
  private final Map<String, HashMap<String, LinkedList<SubResp>>> db;
  private final int partitions;

  public InMemoryMsgSub(Map<String, HashMap<String, LinkedList<SubResp>>> db, int partitions) {
    this.db = db;
    this.partitions = partitions;
  }

  @Override
  public Flux<Try<SubResp>> sub(String topicId, String partition, long offset) {
    return Flux.fromIterable(db.get(topicId).get(partition)).map(Try::success);
  }
}
