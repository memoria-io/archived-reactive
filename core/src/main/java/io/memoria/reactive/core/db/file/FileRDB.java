package io.memoria.reactive.core.db.file;

import io.memoria.reactive.core.db.Msg;
import io.memoria.reactive.core.db.RDB;
import io.memoria.reactive.core.file.RFile;
import io.memoria.reactive.core.file.RFiles;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.function.Function;

public record FileRDB<T>(String topic, Path path, TextTransformer transformer, Class<T> tClass) implements RDB<T> {
  public static final String FILE_EXT = ".json";
  private static final Logger log = LoggerFactory.getLogger(FileRDB.class.getName());

  @Override
  public Mono<Long> index() {
    return sortedList(path).last()
                           .map(i -> i + 1)
                           .doOnError(NoSuchElementException.class, t -> emptyDirectory(path))
                           .onErrorResume(NoSuchElementException.class, t -> Mono.just(0L));
  }

  @Override
  public Flux<Long> publish(Flux<Msg<T>> msgs) {
    var rFiles = msgs.map(m -> toRFile(path, m, transformer::serialize));
    return RFiles.publish(rFiles).map(FileRDB::toIndex);
  }

  @Override
  public Mono<List<Msg<T>>> read(int offset) {
    var deserialize = transformer.deserialize(tClass);
    return RFiles.readDir(path).map(map -> map.map(file -> toMsg(file, deserialize)).drop(offset));
  }

  @Override
  public Flux<Msg<T>> subscribe(int offset) {
    var deserialize = transformer.deserialize(tClass);
    return RFiles.subscribe(path, offset).map(file -> toMsg(file, deserialize));
  }

  @Override
  public Mono<List<Long>> write(List<Msg<T>> msgs) {
    return RFiles.write(msgs.map(msg -> toRFile(path, msg, transformer::serialize))).map(l -> l.map(FileRDB::toIndex));
  }

  static void emptyDirectory(Path p) {
    log.info("Empty directory" + p);
  }

  static Flux<Long> sortedList(Path path) {
    return RFiles.list(path).flatMapMany(Flux::fromIterable).map(FileRDB::toIndex).sort();
  }

  static long toIndex(Path path) {
    var idxStr = path.getFileName().toString().replace(FILE_EXT, "");
    return Long.parseLong(idxStr);
  }

  static <T> Msg<T> toMsg(RFile file, Function<String, Try<T>> fn) {
    var idx = toIndex(file.path());
    var content = fn.apply(file.content()).get();
    return new Msg<>(idx, content);
  }

  static Path toPath(Path path, long index) {
    return path.resolve(index + FILE_EXT);
  }

  static <T> RFile toRFile(Path path, Msg<T> msg, Function1<T, Try<String>> fn) {
    var p = toPath(path, msg.id());
    var content = fn.apply(msg.body()).get();
    return new RFile(p, content);
  }
}
