package com.marmoush.jutils.adapter.msgbus.memory;

import com.marmoush.jutils.domain.port.msgbus.MsgProducer;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.ProducerResp;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static io.vavr.control.Option.some;

public class InMemoryMsgProducer implements MsgProducer<Integer> {
  private final Map<String, HashMap<String, LinkedList<Msg>>> db;

  public InMemoryMsgProducer(Map<String, HashMap<String, LinkedList<Msg>>> db) {
    this.db = db;
  }

  @Override
  public Flux<Try<ProducerResp<Integer>>> produce(String topic, String partition, Flux<Msg> msgFlux) {
    return msgFlux.map(msg -> {
      db.putIfAbsent(topic, new HashMap<>());
      db.get(topic).putIfAbsent(partition, new LinkedList<>());
      db.get(topic).get(partition).addLast(msg);
      int offset = db.get(topic).get(partition).size() - 1;
      return Try.success(new ProducerResp<>(some(offset)));
    });
  }
}
