package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.StreamRepo;
import reactor.core.publisher.Flux;

import java.util.List;

public record MemStream<T>(List<T> store) implements StreamRepo<T> {

  @Override
  public Flux<T> publish(Flux<T> msgs) {
    return msgs.map(msg -> {
      store.add(msg);
      return msg;
    });
  }

  @Override
  public Flux<T> subscribe(long offset) {
    return Flux.fromIterable(store).skip(offset);
  }
}
