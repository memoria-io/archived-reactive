package io.memoria.reactive.core.stream.file;

import io.memoria.reactive.core.file.RFiles;
import io.memoria.reactive.core.id.FileIdGenerator;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.StreamDB;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record RFileStreamDB<T>(String path, String topic, TextTransformer transformer, Class<T> tClass)
        implements StreamDB<T> {

  @Override
  public Flux<Id> publish(Flux<T> msgs) {
    var fileIdGeneratorMono = RFiles.lastFileName(path).map(FileIdGenerator::startFrom);
    var msgsStrFlux = msgs.map(transformer::serialize).map(Try::get);
    var files = fileIdGeneratorMono.flatMapMany(idGen -> msgsStrFlux.map(m -> Tuple.of(idGen.get().value(), m)));
    return RFiles.publish(path, files).map(Id::of);
  }

  @Override
  public Mono<LinkedHashMap<Id, T>> read(long offset) {
    return RFiles.readDirectory(path).map(mp -> mp.map(this::deserialize));
  }

  @Override
  public Flux<Tuple2<Id, T>> subscribe(long offset) {
    return RFiles.subscribe(path, offset).map(mp -> mp.map(this::deserialize));
  }

  @Override
  public Mono<Map<Id, T>> write(List<T> events) {
    //    return RFile.write(path, ); 
    return null;
  }

  private Tuple2<Id, T> deserialize(String t1, String t2) {
    return Tuple.of(Id.of(t1), transformer.deserialize(t2, tClass).get());
  }
}
