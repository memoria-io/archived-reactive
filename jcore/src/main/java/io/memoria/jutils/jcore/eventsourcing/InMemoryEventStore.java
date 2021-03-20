package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

import static io.memoria.jutils.jcore.vavr.ReactorVavrUtils.toMono;

public record InMemoryEventStore(ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<Event>>> store)
        implements EventStore {

  @Override
  public Mono<Event> lastEvent(String topic, int partition) {
    return toMono(Try.of(() -> store.get(topic).get(partition).last()).toOption());
  }

  @Override
  public Mono<List<Event>> publish(String topic, int partition, List<Event> events) {
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

  @Override
  public Flux<Event> subscribe(String topic, int partition, long offset) {
    return Mono.fromCallable(() -> store.get(topic)).flatMapMany(p -> Flux.fromIterable(p.get(partition)));
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return null;
  }

  @Override
  public Mono<Integer> createTopic(String topic, int partitions, int replicationFactor) {
    return null;
  }

  @Override
  public Mono<Integer> nOfPartitions(String topic) {
    return null;
  }

  @Override
  public Mono<Long> currentOffset(String topic, int partition) {
    return null;
  }
}
