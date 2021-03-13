package io.memoria.jutils.jcore.eventsourcing;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public record InMemorySubscriber(ConcurrentHashMap<String, ConcurrentHashMap<Integer, Flux<Event>>> store)
        implements EventSubscriber {

  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromCallable(() -> store.containsKey(topic));
  }

  @Override
  public Mono<Predicate<Event>> lastEventPredicate(String topic, int partition) {
    return Mono.fromCallable(() -> store.get(topic))
               .flatMapMany(store -> store.get(partition))
               .last()
               .map(Event::eventId)
               .map(lastEventId -> (Predicate<Event>) ev -> ev.eventId().equals(lastEventId))
               .onErrorReturn(NoSuchElementException.class, ev -> true);
  }

  @Override
  public Flux<Event> subscribe(String topic, int partition, long startOffset) {
    return Mono.fromCallable(() -> store.get(topic)).flatMapMany(p -> p.get(partition)).skip(startOffset);
  }
}
