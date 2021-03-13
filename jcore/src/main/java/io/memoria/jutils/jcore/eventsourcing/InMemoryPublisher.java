package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public record InMemoryPublisher(ConcurrentHashMap<String, ConcurrentHashMap<Integer, Flux<Event>>> store)
        implements EventPublisher {
  @Override
  public Mono<List<Event>> apply(String topic, Integer partition, List<Event> events) {
    var eventFlux = Flux.fromIterable(events);
    return Mono.fromCallable(() -> {
      store.computeIfPresent(topic, (topicKey, oldTopic) -> {
        oldTopic.computeIfPresent(partition, (partitionKey, previousList) -> previousList.concatWith(eventFlux));
        oldTopic.computeIfAbsent(partition, partitionKey -> eventFlux);
        return oldTopic;
      });
      store.computeIfAbsent(topic, topicKey -> {
        var map = new ConcurrentHashMap<Integer, Flux<Event>>();
        map.put(partition, eventFlux);
        return map;
      });
      return events;
    });
  }
}
