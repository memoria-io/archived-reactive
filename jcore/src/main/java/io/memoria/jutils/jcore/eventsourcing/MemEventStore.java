package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

import static io.memoria.jutils.jcore.vavr.ReactorVavrUtils.toMono;

public record MemEventStore(String topic,
                            int partition,
                            long offset,
                            ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<Event>>> store)
        implements EventStore {

  @Override
  public Mono<Event> last() {
    return toMono(Try.of(() -> store.get(topic).get(partition).last()));
  }

  @Override
  public Mono<Long> publish(List<Event> msgs) {
    return Mono.fromCallable(() -> {
      store.computeIfPresent(topic, (topicKey, oldTopic) -> {
        oldTopic.computeIfPresent(partition, (partitionKey, previousList) -> previousList.appendAll(msgs));
        oldTopic.computeIfAbsent(partition, partitionKey -> msgs);
        return oldTopic;
      });
      store.computeIfAbsent(topic, topicKey -> {
        var map = new ConcurrentHashMap<Integer, List<Event>>();
        map.put(partition, msgs);
        return map;
      });
      return (long) store.get(topic).get(partition).size();
    });
  }

  @Override
  public Flux<Event> subscribe(long offset) {
    return toMono(Try.of(() -> store.get(topic).get(partition))).flatMapMany(Flux::fromIterable).skip(offset);
  }
}
