package io.memoria.reactive.core.db.mem;

import io.memoria.reactive.core.db.RDB;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record MemRDB<T>(java.util.List<T> db) implements RDB<T> {
  @Override
  public Mono<Long> index() {
    return Mono.fromCallable(() -> (long) db.size());
  }

  @Override
  public Flux<T> publish(Flux<T> msgs) {
    return msgs.map(msg -> {
      db.add(msg);
      return msg;
    });
  }

  @Override
  public Mono<List<T>> read(int offset) {
    return Mono.fromCallable(() -> List.ofAll(db).drop(offset));
  }

  @Override
  public Mono<Integer> size() {
    return Mono.fromCallable(db::size);
  }

  @Override
  public Flux<T> subscribe(int offset) {
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
