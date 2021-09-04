package io.memoria.reactive.core.stream.file;

import io.memoria.reactive.core.file.RFiles;
import io.memoria.reactive.core.id.Id;
import io.memoria.reactive.core.id.SerialIdGenerator;
import io.memoria.reactive.core.stream.StreamDB;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

import static io.memoria.reactive.core.stream.file.RFileStreamDBUtils.toPath;

public record RFileStreamDB<T>(Path path, String topic, TextTransformer transformer, Class<T> tClass)
        implements StreamDB<T> {
  public static final String FILE_EXT = ".json";
  private static final Logger log = LoggerFactory.getLogger(RFileStreamDB.class.getName());

  @Override
  public Flux<Long> publish(Flux<T> msgs) {
    var idGenMono = RFileStreamDBUtils.startIndex(path).map(SerialIdGenerator::new);
    var msgsStrFlux = msgs.map(transformer::serialize).map(Try::get);
    var files = idGenMono.flatMapMany(idGen -> msgsStrFlux.map(m -> Tuple.of(toPath(path, idGen.get()), m)));
    return RFiles.publish(files).map(RFileStreamDBUtils::toIndex);
  }

  @Override
  public Mono<LinkedHashMap<Long, T>> read(long offset) {
    //    return RFiles.readDirectory(path).map(mp -> mp.map(this::deserialize));
    return null;
  }

  @Override
  public Flux<Tuple2<Long, T>> subscribe(long offset) {
    //    return RFiles.subscribe(path, offset).map(mp -> mp.map(this::deserialize));
    return null;
  }

  @Override
  public Mono<List<Long>> write(List<T> events) {
    //    return RFile.write(path, ); 
    return null;
  }

  private Tuple2<Id, T> deserialize(String t1, String t2) {
    return Tuple.of(Id.of(t1), transformer.deserialize(t2, tClass).get());
  }
}
