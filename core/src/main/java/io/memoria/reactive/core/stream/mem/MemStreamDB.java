package io.memoria.reactive.core.stream.mem;

import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.StreamDB;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public record MemStreamDB<T>(java.util.List<T> db) implements StreamDB<T> {
  @Override
  public Flux<Id> publish(Flux<T> msgs) {
    return msgs.map(msg -> {
      db.add(msg);
      return db.size();
    }).map(Id::of);
  }

  @Override
  public Mono<LinkedHashMap<Id, T>> read(long offset) {
    return Flux.fromIterable(db)
               .zipWith(Flux.range(0, db.size()))
               .map(this::content)
               .skip(offset)
               .reduce(LinkedHashMap.empty(), LinkedHashMap::put);
  }

  @Override
  public Flux<Tuple2<Id, T>> subscribe(long offset) {
    return Flux.fromIterable(db).zipWith(Flux.range(0, db.size())).map(this::content).skip(offset);
  }

  private Tuple2<Id, T> content(reactor.util.function.Tuple2<T, Integer> t) {
    return Tuple.of(Id.of(t.getT2()), t.getT1());
  }

  @Override
  public Mono<Map<Id, T>> write(List<T> msgs) {
    return Mono.fromCallable(() -> {
      var start = db.size();
      db.addAll(msgs.toJavaList());
      return msgs.zipWithIndex().map(t -> Tuple.of(Id.of(t._2 + start), t._1)).toMap(Function.identity());
    });
  }
}
