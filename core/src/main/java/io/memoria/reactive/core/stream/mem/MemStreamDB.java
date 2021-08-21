package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.StreamDB;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record MemStreamDB<T>(java.util.List<T> db) implements StreamDB<T> {
  @Override
  public Flux<T> publish(Flux<T> msgs) {
    return msgs.map(msg -> {
      db.add(msg);
      return msg;
    });
  }

  @Override
  public Mono<List<T>> read(long offset) {
    return Flux.fromIterable(db).skip(offset).collectList().map(List::ofAll);
  }

  @Override
  public Flux<T> subscribe(long offset) {
    return Flux.fromIterable(db).skip(offset);
  }

  @Override
  public Mono<List<T>> write(List<T> msgs) {
    return Mono.fromCallable(() -> {
      db.addAll(msgs.toJavaList());
      return msgs;
    });
  }
}
