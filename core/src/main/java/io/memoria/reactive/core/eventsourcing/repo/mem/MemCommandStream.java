package io.memoria.reactive.core.eventsourcing.repo.mem;

import io.memoria.reactive.core.eventsourcing.Command;
import io.memoria.reactive.core.eventsourcing.CommandStream;
import reactor.core.publisher.Flux;

import java.util.List;

public record MemCommandStream(List<Command> store) implements CommandStream {

  @Override
  public Flux<Command> publish(Flux<Command> cmds) {
    return cmds.map(cmd -> {
      store.add(cmd);
      return cmd;
    });
  }

  @Override
  public Flux<Command> subscribe(long offset) {
    return Flux.fromIterable(store).skip(offset);
  }
}
