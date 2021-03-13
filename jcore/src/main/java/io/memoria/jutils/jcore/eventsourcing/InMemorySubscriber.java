package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public record InMemorySubscriber(ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<Event>>> store)
        implements EventSubscriber {

  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromCallable(() -> store.containsKey(topic));
  }

  @Override
  public Mono<Predicate<Event>> lastEventPredicate(String topic, int partition) {
    return Mono.fromCallable(() -> store.get(topic))
               .map(store -> store.get(partition))
               .map(List::last)
               .map(event -> ev -> event.eventId().equals(ev.eventId()));
  }

  @Override
  public Flux<Event> subscribe(String topic, int partition, long startOffset) {
    return Mono.fromCallable(() -> store.get(topic)).flatMapMany(p -> Flux.fromIterable(p.get(partition)));
  }
}
