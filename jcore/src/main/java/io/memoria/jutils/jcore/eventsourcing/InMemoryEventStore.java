package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public record InMemoryEventStore(ConcurrentHashMap<String, ConcurrentHashMap<Integer, Flux<Event>>> store)
        implements EventStore {
  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromCallable(() -> store.containsKey(topic));
  }

  @Override
  public Mono<Predicate<Event>> endPred(String topic, int partition) {
    return Mono.fromCallable(() -> store.get(topic))
               .flatMapMany(store -> store.get(partition))
               .last()
               .map(Event::eventId)
               .map(lastEventId -> (Predicate<Event>) ev -> ev.eventId().equals(lastEventId))
               .onErrorReturn(NoSuchElementException.class, ev -> true);
  }

  @Override
  public Mono<List<Event>> publish(String topic, int partition, List<Event> events) {
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

  @Override
  public Flux<Event> subscribe(String topic, int partition, int offset) {
    return Mono.fromCallable(() -> store.get(topic)).flatMapMany(p -> p.get(partition)).skip(offset);
  }
}
