package io.memoria.reactive.core.db.file;

import io.memoria.reactive.core.db.Msg;
import io.memoria.reactive.core.db.RDB;
import io.memoria.reactive.core.file.RFile;
import io.memoria.reactive.core.file.RFiles;
import io.memoria.reactive.core.text.TextTransformer;
import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.NoSuchElementException;

public record FileRDB<T extends Msg>(Path path, TextTransformer transformer, Class<T> tClass) implements RDB<T> {
  public static final String FILE_EXT = ".json";
  private static final Logger log = LoggerFactory.getLogger(FileRDB.class.getName());

  @Override
  public Mono<Long> currentIndex() {
    return FileRDBUtils.sortedList(path)
                       .last()
                       .map(i -> i + 1)
                       .doOnError(NoSuchElementException.class, t -> infoIndexZero(path))
                       .onErrorResume(NoSuchElementException.class, t -> Mono.just(0L));
  }

  @Override
  public Flux<Long> publish(Flux<T> msgs) {
    var rFiles = msgs.concatMap(m -> toRFile(path, m));
    return RFiles.publish(rFiles).map(FileRDBUtils::toIndex);
  }

  @Override
  public Mono<List<T>> read(int offset) {
    var deserialize = transformer.deserialize(tClass);
    return RFiles.readDir(path)
                 .flatMapMany(Flux::fromIterable)
                 .map(RFile::content)
                 .concatMap(deserialize)
                 .skip(offset)
                 .collectList()
                 .map(List::ofAll);
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
                              .concatMap(deserialize)
                              .sort();
    var newFiles = RFiles.subscribe(path).map(RFile::content).concatMap(deserialize);
    return Flux.concat(existingFiles, newFiles).skip(offset);
  }

  @Override
  public Mono<List<Long>> write(List<T> msgs) {
    return Flux.fromIterable(msgs)
               .concatMap(msg -> toRFile(path, msg))
               .collectList()
               .map(List::ofAll)
               .flatMap(RFiles::write)
               .map(l -> l.map(FileRDBUtils::toIndex));
  }

  private Mono<RFile> toRFile(Path path, T msg) {
    var p = FileRDBUtils.toPath(path, msg.id());
    return transformer.serialize(msg).map(content -> new RFile(p, content));
  }

  private static void infoIndexZero(Path p) {
    log.info("Directory %s was empty returning index = zero".formatted(p));
  }
}
