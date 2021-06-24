package io.memoria.jutils.jcore.eventsourcing.repo;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public class SQLEventRepo implements EventRepo {
  
  
  @Override
  public Mono<Void> add(String aggregate, List<Event> event) {
    return null;
  }

  @Override
  public Mono<List<Event>> find(String aggregate) {
    return null;
  }
}
