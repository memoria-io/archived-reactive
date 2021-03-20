package io.memoria.jutils.jcore.msgbus;

import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

public record MemPublisher(String topic,
                           int partition,
                           ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<String>>> store)
        implements MsgBusPublisher {

  @Override
  public Mono<Void> beginTransaction() {
    return Mono.empty();
  }

  @Override
  public Mono<Void> abortTransaction() {
    return Mono.empty();
  }

  @Override
  public Mono<Void> commitTransaction() {
    return Mono.empty();
  }

  @Override
  public Mono<Long> publish(String msg) {
    return Mono.fromCallable(() -> {
      store.computeIfPresent(topic, (topicKey, oldTopic) -> {
        oldTopic.computeIfPresent(partition, (partitionKey, previousList) -> previousList.append(msg));
        oldTopic.computeIfAbsent(partition, partitionKey -> List.of(msg));
        return oldTopic;
      });
      store.computeIfAbsent(topic, topicKey -> {
        var map = new ConcurrentHashMap<Integer, List<String>>();
        map.put(partition, List.of(msg));
        return map;
      });
      return (long) store.get(topic).get(partition).size();
    });
  }
}
