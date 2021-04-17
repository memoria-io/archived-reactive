package io.memoria.jutils.jcore.stream.mem;

import io.memoria.jutils.jcore.stream.StreamRepo;
import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

import static io.memoria.jutils.jcore.vavr.ReactorVavrUtils.toMono;

public record MemStream(String topic,
                        int partition,
                        ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<String>>> store)
        implements StreamRepo {

  @Override
  public Mono<String> last() {
    return Mono.fromCallable(() -> store.get(topic).get(partition).last())
               .onErrorResume(NullPointerException.class, t -> Mono.empty());
  }

  @Override
  public Mono<Long> publish(List<String> msgs) {
    return Mono.fromCallable(() -> {
      store.computeIfPresent(topic, (topicKey, oldTopic) -> {
        oldTopic.computeIfPresent(partition, (partitionKey, previousList) -> previousList.appendAll(msgs));
        oldTopic.computeIfAbsent(partition, partitionKey -> msgs);
        return oldTopic;
      });
      store.computeIfAbsent(topic, topicKey -> {
        var map = new ConcurrentHashMap<Integer, List<String>>();
        map.put(partition, msgs);
        return map;
      });
      return (long) store.get(topic).get(partition).size();
    });
  }

  @Override
  public Flux<String> subscribe(long offset) {
    return toMono(Try.of(() -> store.get(topic).get(partition))).flatMapMany(Flux::fromIterable).skip(offset);
  }
}
