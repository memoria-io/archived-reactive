package io.memoria.reactive.core.stream.file;

import io.memoria.reactive.core.file.RFile;
import io.memoria.reactive.core.id.FileIdGenerator;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.stream.StreamDB;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public record RFileStreamDB<T>(String path, String topic, TextTransformer transformer, Class<T> tClass)
        implements StreamDB<T> {

  @Override
  public Flux<Id> publish(Flux<T> msgs) {
    var fileIdGeneratorMono = RFile.lastFileName(path).map(FileIdGenerator::startFrom);
    var msgsStrFlux = msgs.map(transformer::serialize).map(Try::get);
    var files = fileIdGeneratorMono.flatMapMany(idGen -> msgsStrFlux.map(m -> Tuple.of(idGen.get().value(), m)));
    return RFile.publish(path, files).map(Id::of);
  }

  @Override
  public Mono<List<T>> read(long offset) {
    var deserialize = transformer.deserialize(tClass);
    return RFile.readDirectory(path).map(list -> list.map(deserialize).map(Try::get));
  }

  @Override
  public Flux<T> subscribe(long offset) {
    var deserialize = transformer.deserialize(tClass);
    return RFile.subscribe(path, offset).map(deserialize).map(Try::get);
  }

  @Override
  public Mono<Map<Id, T>> write(List<T> events) {
    //    return RFile.write(path, ); 
    return null;
  }
}
