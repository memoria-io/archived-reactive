package com.marmoush.jutils.adapter.msgbus.memory;

import com.marmoush.jutils.domain.port.msgbus.MsgPublisher;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.PublishResponse;
import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryMsgPublisher implements MsgPublisher {
  private final Map<String, HashMap<Integer, LinkedList<Msg>>> db;

  public InMemoryMsgPublisher(Map<String, HashMap<Integer, LinkedList<Msg>>> db) {
    this.db = db;
  }

  @Override
  public Mono<Try<PublishResponse>> publish(String topic, int partition, Msg msg) {
    db.putIfAbsent(topic, new HashMap<>()).putIfAbsent(partition, new LinkedList<>()).addLast(msg);
    long offset = db.get(topic).get(partition).size() - 1;
    return Mono.just(Try.success(new PublishResponse(topic,
                                                     partition,
                                                     Option.of(offset),
                                                     Option.of(LocalDateTime.now()))));
  }
}
