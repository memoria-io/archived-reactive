package io.memoria.reactive.core.db.file;

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
import java.util.concurrent.atomic.AtomicLong;

public final class FileRDB<T> implements RDB<T> {

  private static final Logger log = LoggerFactory.getLogger(FileRDB.class.getName());

  private final Path path;
  private final TextTransformer transformer;
  private final Class<T> tClass;
  private final AtomicLong idx;

  public FileRDB(long idx, Path path, TextTransformer transformer, Class<T> tClass) {
    this.path = path;
    this.transformer = transformer;
    this.tClass = tClass;
    this.idx = new AtomicLong(idx);
  }

  @Override
  public Mono<Long> index() {
    return RFiles.index(path);
  }

  @Override
  public Flux<T> publish(Flux<T> msgs) {
    return msgs.concatMap(this::write);
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
    return RFiles.list(path).count().map(Long::intValue);
  }

  @Override
  public Flux<T> subscribe(int offset) {
    var deserialize = transformer.deserialize(tClass);
    var existingFiles = RFiles.readDir(path).flatMapMany(Flux::fromIterable).map(RFile::content).concatMap(deserialize);
    var newFiles = RFiles.subscribe(path).map(RFile::content).concatMap(deserialize);
    return Flux.concat(existingFiles, newFiles).skip(offset);
  }

  @Override
  public Mono<List<T>> write(List<T> msgs) {
    return Flux.fromIterable(msgs).concatMap(this::write).collectList().map(List::ofAll);
  }

  private Mono<T> write(T msg) {
    return transformer.serialize(msg)
                      .map(content -> new RFile(RFiles.toPath(path, idx.getAndIncrement()), content))
                      .flatMap(RFiles::write)
                      .thenReturn(msg);
  }
}
