package io.memoria.reactive.core.rsdb.file;

import io.memoria.reactive.core.rsdb.RSDB;
import io.memoria.reactive.core.file.RFile;
import io.memoria.reactive.core.file.RFiles;
import io.vavr.Function1;
import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

public final class FileRSDB<T> implements RSDB<T> {

  private static final Logger log = LoggerFactory.getLogger(FileRSDB.class.getName());

  private final Path path;
  private final Function1<T, Mono<String>> serialize;
  private final Function1<String, Mono<T>> deserialize;
  private final AtomicLong idx;

  public FileRSDB(long idx, Path path, Function1<T, Mono<String>> serialize, Function1<String, Mono<T>> deserialize) {
    this.path = path;
    this.idx = new AtomicLong(idx);
    this.serialize = serialize;
    this.deserialize = deserialize;
  }

  @Override
  public Mono<T> publish(T msg) {
    return serialize.apply(msg)
                    .map(content -> new RFile(RFiles.toPath(path, idx.getAndIncrement()), content))
                    .flatMap(RFiles::write)
                    .thenReturn(msg);
  }

  @Override
  public Mono<List<T>> read(int offset) {
    return RFiles.readDir(path)
                 .flatMapMany(Flux::fromIterable)
                 .map(RFile::content)
                 .concatMap(deserialize)
                 .skip(offset)
                 .collectList()
                 .map(List::ofAll);
  }

  @Override
  public Flux<T> subscribe(int offset) {
    var existingFiles = RFiles.readDir(path).flatMapMany(Flux::fromIterable).map(RFile::content).concatMap(deserialize);
    var newFiles = RFiles.subscribe(path).map(RFile::content).concatMap(deserialize);
    return Flux.concat(existingFiles, newFiles).skip(offset);
  }
}
