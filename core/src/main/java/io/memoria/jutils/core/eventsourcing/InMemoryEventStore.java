package io.memoria.jutils.core.eventsourcing;

import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

public record InMemoryEventStore(Map<String, ArrayList<Event>> db) implements EventStore {

  @Override
  public Flux<Event> add(String topic, Flux<Event> events) {
    return Mono.fromRunnable(() -> {
      if (!db.containsKey(topic)) {
        db.put(topic, new ArrayList<>());
      }
    }).thenMany(events.map(e -> {
      db.get(topic).add(e);
      return e;
    }));
  }

  @Override
  public Mono<Boolean> exists(String topic) {
    return Mono.fromCallable(() -> db.containsKey(topic));
  }

  @Override
  public Flux<Event> stream(String topic) {
    return Mono.fromCallable(() -> Option.of(db.get(topic)))
               .map(o -> o.getOrElse(new ArrayList<>()))
               .flatMapMany(Flux::fromIterable);
  }
}
