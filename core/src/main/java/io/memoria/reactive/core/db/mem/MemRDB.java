package io.memoria.reactive.core.db.mem;

import io.memoria.reactive.core.db.Msg;
import io.memoria.reactive.core.db.RDB;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record MemRDB<T extends Msg>(java.util.List<T> db) implements RDB<T> {
  @Override
  public Mono<Long> currentIndex() {
    return Mono.fromCallable(() -> db.size() - 1L);
  }

  @Override
  public Flux<Long> publish(Flux<T> msgs) {
    return msgs.map(msg -> {
      db.add(msg);
      return msg;
    }).map(Msg::id);
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
  public Mono<List<Long>> write(List<T> msgs) {
    return Mono.fromCallable(() -> {
      db.addAll(msgs.toJavaList());
      return msgs;
    }).map(l -> l.map(Msg::id));
  }
}
