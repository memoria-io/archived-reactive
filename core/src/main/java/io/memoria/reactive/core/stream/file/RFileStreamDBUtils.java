package io.memoria.reactive.core.stream.file;

import io.memoria.reactive.core.file.RFiles;
import io.memoria.reactive.core.id.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.NoSuchElementException;

import static io.memoria.reactive.core.stream.file.RFileStreamDB.FILE_EXT;

class RFileStreamDBUtils {
  private static final Logger log = LoggerFactory.getLogger(RFileStreamDBUtils.class.getName());

  private RFileStreamDBUtils() {}

  static void emptyDirectory(Path p) {
    log.info("Empty directory" + p);
  }

  static Mono<Long> startIndex(Path path) {
    return sortedList(path).last()
                           .map(i -> i + 1)
                           .doOnError(NoSuchElementException.class, t -> emptyDirectory(path))
                           .onErrorResume(NoSuchElementException.class, t -> Mono.just(0L));
  }

  static Flux<Long> sortedList(Path path) {
    return RFiles.list(path).flatMapMany(Flux::fromIterable).map(RFileStreamDBUtils::toIndex).sort();
  }

  static Flux<String> sortedContent(Path path) {
    return sortedList(path).map(p -> p + FILE_EXT).map(path::resolve).flatMap(RFiles::read);
  }

  static long toIndex(Path path) {
    var idxStr = path.getFileName().toString().replace(FILE_EXT, "");
    return Long.parseLong(idxStr);
  }

  static Path toPath(Path path, Id id) {
    return toPath(path, Long.parseLong(id.value()));
  }

  static Path toPath(Path path, long index) {
    return path.resolve(index + FILE_EXT);
  }
}
