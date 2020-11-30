package io.memoria.jutils.core.eventsourcing.event;

import io.memoria.jutils.core.value.Id;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record InMemoryEventStore(Map<Id, List<Event>> db) implements EventStore {

  @Override
  public Mono<Event> add(Id topic, Event e) {
    return Mono.fromRunnable(() -> {
      if (!db.containsKey(topic)) {
        db.put(topic, new ArrayList<>());
      }
      db.get(topic).add(e);
    }).thenReturn(e);
  }

  @Override
  public Mono<List<Event>> get(Id topic) {
    return Mono.fromCallable(() -> Option.of(db.get(topic))).map(o -> o.getOrElse(new ArrayList<>()));
  }
}
