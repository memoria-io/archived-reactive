package io.memoria.jutils.core.eventsourcing.stateless;

import io.memoria.jutils.core.eventsourcing.Command;
import io.memoria.jutils.core.eventsourcing.CommandHandler;
import io.memoria.jutils.core.eventsourcing.Event;
import io.memoria.jutils.core.value.Id;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

public class InMemoryCommandHandler<C extends Command> implements CommandHandler<C> {
  private final Map<String, Lock> locks;
  private final ConcurrentMap<String, List<Event>> db;

  @Override
  public Mono<List<Event>> add(String topic, List<Event> events) {
    return Mono.fromRunnable(() -> {
      var prev = db.putIfAbsent(topic, events);
      if (prev != null)
        prev.addAll(events);
    });
  }

  @Override
  public Mono<io.vavr.collection.List<Event>> apply(Id id, C c) {
    return null;
  }

  @Override
  public Mono<List<Event>> get(String topic, Id id) {
    return get(topic).map(l -> l.stream().filter(e -> e.id().equals(id)).collect(Collectors.toList()));
  }

  @Override
  public Mono<List<Event>> get(String topic) {
    return Mono.fromCallable(() -> Option.of(db.get(topic))).map(o -> o.getOrElse(new ArrayList<>()));
  }
}
