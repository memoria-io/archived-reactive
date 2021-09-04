package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.stream.StreamDB;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record MemStreamDB<T>(java.util.List<T> db) implements StreamDB<T> {
  @Override
  public Flux<Long> publish(Flux<T> msgs) {
    return msgs.map(msg -> {
      db.add(msg);
      return Long.valueOf(db.size());
    });
  }

  @Override
  public Mono<LinkedHashMap<Long, T>> read(long offset) {
    return Flux.fromIterable(db)
               .zipWith(Flux.range(0, db.size()))
               .map(this::content)
               .skip(offset)
               .reduce(LinkedHashMap.empty(), LinkedHashMap::put);
  }

  @Override
  public Flux<Tuple2<Long, T>> subscribe(long offset) {
    return Flux.fromIterable(db).zipWith(Flux.range(0, db.size())).map(this::content).skip(offset);
  }

  @Override
  public Mono<List<Long>> write(List<T> msgs) {
    return Mono.fromCallable(() -> {
      var start = db.size();
      db.addAll(msgs.toJavaList());
      return List.range(start, db.size()).map(Long::valueOf);
    });
  }

  private Tuple2<Long, T> content(reactor.util.function.Tuple2<T, Integer> t) {
    return Tuple.of(Long.valueOf(t.getT2()), t.getT1());
  }
}
