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
  public Mono<Void> createTopic(String topic, int partitions, int replicationFactor) {
    return Mono.fromCallable(() -> createTopic(topic, partitions)).then();
  }

  private int createTopic(String topic, int partitions) throws ESException {
    if (store.containsKey(topic))
      throw ESException.create("Topic already exists");
    else {
      var map = new ConcurrentHashMap<Integer, List<Event>>();
      List.range(0, partitions).forEach(i -> map.put(i, List.empty()));
      store.put(topic, map);
      return partitions;
    }
  }

  @Override
  public Mono<Long> currentOffset(String topic, int partition) {
    return toMono(Try.of(() -> (long) store.get(topic).get(partition).size()));
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromCallable(() -> store.containsKey(topic));
  }

  @Override
  public Mono<Event> lastEvent(String topic, int partition) {
    return toMono(Try.of(() -> store.get(topic).get(partition).last()).toOption());
  }

  @Override
  public Mono<Integer> nOfPartitions(String topic) {
    return toMono(Try.of(() -> this.store.get(topic).size()));
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
}
