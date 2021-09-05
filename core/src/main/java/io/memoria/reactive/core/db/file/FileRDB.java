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

public record FileRDB<T extends Msg>(String topic, Path path, TextTransformer transformer, Class<T> tClass)
        implements RDB<T> {
  public static final String FILE_EXT = ".json";
  private static final Logger log = LoggerFactory.getLogger(FileRDB.class.getName());

  @Override
  public Mono<Long> currentIndex() {
    return sortedList(path).last()
                           .map(i -> i + 1)
                           .doOnError(NoSuchElementException.class, t -> emptyDirectory(path))
                           .onErrorResume(NoSuchElementException.class, t -> Mono.just(0L));
  }

  @Override
  public Flux<Long> publish(Flux<T> msgs) {
    var rFiles = msgs.map(m -> toRFile(path, m, transformer::serialize));
    return RFiles.publish(rFiles).map(FileRDB::toIndex);
  }

  @Override
  public Mono<List<T>> read(int offset) {
    var deserialize = transformer.deserialize(tClass);
    return RFiles.readDir(path).map(files -> files.map(RFile::content).map(deserialize).map(Try::get).drop(offset));
  }

  @Override
  public Mono<Integer> size() {
    return RFiles.list(path).map(List::size);
  }

  @Override
  public Flux<T> subscribe(int offset) {
    var deserialize = transformer.deserialize(tClass);
    var existingFiles = RFiles.readDir(path)
                              .flatMapMany(Flux::fromIterable)
                              .map(RFile::content)
                              .map(deserialize)
                              .map(Try::get)
                              .sort();
    var newFiles = RFiles.subscribe(path).map(RFile::content).map(deserialize).map(Try::get);
    return Flux.concat(existingFiles, newFiles).skip(offset);
  }

  @Override
  public Mono<List<Long>> write(List<T> msgs) {
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

  static Path toPath(Path path, long index) {
    return path.resolve(index + FILE_EXT);
  }

  private RFile toRFile(Path path, T msg, Function1<T, Try<String>> fn) {
    var p = toPath(path, msg.id());
    var content = fn.apply(msg).get();
    return new RFile(p, content);
  }
}
