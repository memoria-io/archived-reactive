package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.collection.List;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

public record InMemoryPublisher(ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<Event>>> store)
        implements EventPublisher {
  @Override
  public Mono<List<Event>> apply(String topic, Integer partition, List<Event> events) {
    return Mono.fromCallable(() -> {
      store.computeIfPresent(topic, (topicKey, oldTopic) -> {
        oldTopic.computeIfPresent(partition, (partitionKey, previousList) -> previousList.appendAll(events));
        oldTopic.computeIfAbsent(partition, partitionKey -> events);
        return oldTopic;
      });
      store.computeIfAbsent(topic, topicKey -> {
        var map = new ConcurrentHashMap<Integer, List<Event>>();
        map.put(partition, events);
        return map;
      });
      return events;
    });
  }
}
