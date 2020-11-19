package io.memoria.jutils.core.eventsourcing.event;

import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public record InMemoryEventStore(Map<String, ArrayList<Event>> db) implements EventStore {

  @Override
  public Flux<String> add(String streamId, Flux<Event> events) {
    //    return Mono.fromRunnable(() -> {
    //      if (!db.containsKey(streamId)) {
    //        db.put(streamId, new ArrayList<>());
    //      }
    //    }).map(m -> m)
    return null;
  }

  @Override
  public Mono<Boolean> exists(String streamId) {
    return Mono.fromCallable(() -> db.containsKey(streamId));
  }

  @Override
  public Flux<Event> stream(String streamId) {
    //    return Mono.fromCallable(() -> Option.of(db.get(streamId)))
    //               .map(o -> o.getOrElse(new ArrayList<>()))
    //               .flatMapMany(Flux::fromIterable);
    return null;
  }
}
