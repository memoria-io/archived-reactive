package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

import static io.memoria.jutils.jcore.vavr.ReactorVavrUtils.toMono;

public record MemEventStoreAdmin(ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<Event>>> store)
        implements EventStoreAdmin {

  @Override
  public Mono<Void> createTopic(String topic, int partitions, int replicationFactor) {
    return Mono.fromCallable(() -> createTopic(topic, partitions)).then();
  }

  @Override
  public Mono<Void> increasePartitionsTo(String topic, int partitions) {
    return Mono.fromRunnable(() -> List.range(0, partitions)
                                       .filter(i -> !store.get(topic).containsKey(i))
                                       .map(i -> store.get(topic).put(i, List.empty())));
  }

  @Override
  public Mono<Long> currentOffset(String topic, int partition) {
    return toMono(Try.of(() -> (long) store.get(topic).get(partition).size()));
  }

  @Override
  public Mono<Boolean> exists(String topic, int partition) {
    return Mono.fromCallable(() -> store.containsKey(topic) && store.get(topic).containsKey(partition));
  }

  @Override
  public Mono<Integer> nOfPartitions(String topic) {
    return toMono(Try.of(() -> this.store.get(topic).size()));
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
}
