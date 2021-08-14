package io.memoria.reactive.core.eventsourcing.repo.mem;

import io.memoria.reactive.core.eventsourcing.Event;
import io.memoria.reactive.core.eventsourcing.EventStream;
import reactor.core.publisher.Flux;

import java.util.List;

public record MemEventStream(List<Event> store) implements EventStream {

  @Override
  public Flux<Event> publish(Flux<Event> cmds) {
    return cmds.map(msg -> {
      store.add(msg);
      return msg;
    });
  }

  @Override
  public Flux<Event> subscribe(long offset) {
    return Flux.fromIterable(store).skip(offset);
  }
}
