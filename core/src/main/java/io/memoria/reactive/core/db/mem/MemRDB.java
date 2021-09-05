package io.memoria.reactive.core.db.mem;

import io.memoria.reactive.core.db.Msg;
import io.memoria.reactive.core.db.RDB;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record MemRDB<T>(java.util.List<Msg<T>> db) implements RDB<T> {
  @Override
  public Mono<Long> index() {
    return Mono.fromCallable(() -> db.size() - 1L);
  }

  @Override
  public Flux<Long> publish(Flux<Msg<T>> msgs) {
    return msgs.map(msg -> {
      db.add(msg);
      return msg;
    }).map(Msg::id);
  }

  @Override
  public Mono<List<Msg<T>>> read(int offset) {
    return Mono.fromCallable(() -> List.ofAll(db).drop(offset));
  }

  @Override
  public Flux<Msg<T>> subscribe(int offset) {
    return Flux.fromIterable(db).skip(offset);
  }

  @Override
  public Mono<List<Long>> write(List<Msg<T>> msgs) {
    return Mono.fromCallable(() -> {
      db.addAll(msgs.toJavaList());
      return msgs;
    }).map(l -> l.map(Msg::id));
  }
}
