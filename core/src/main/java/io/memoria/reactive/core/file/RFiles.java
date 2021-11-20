package io.memoria.reactive.core.file;

import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class RFiles {
  private static final Logger log = LoggerFactory.getLogger(RFiles.class.getName());
  private static final BinaryOperator<String> JOIN_LINES = (a, b) -> a + System.lineSeparator() + b;
  public static final String JSON_FILE_EXT = ".json";

  private RFiles() {}

  public static Flux<Path> clean(Path path) {
    return list(path).flatMap(RFiles::delete);
  }

  public static Mono<Path> createDirectory(Path path) {
    return Mono.fromCallable(() -> path.toFile().mkdirs()).thenReturn(path);
  }

  public static Mono<Path> delete(Path path) {
    return Mono.fromCallable(() -> Files.deleteIfExists(path)).thenReturn(path);
  }

  public static Flux<Path> delete(List<Path> files) {
    return Flux.fromIterable(files).concatMap(RFiles::delete);
  }

  public static Mono<Long> index(Path path) {
    return RFiles.list(path)
                 .last()
                 .map(RFiles::toIndex)
                 .map(i -> i + 1)
                 .doOnError(NoSuchElementException.class, t -> log.info(RFiles.infoIndexZero(path)))
                 .onErrorResume(NoSuchElementException.class, t -> Mono.just(0L));
  }

  public static Mono<Path> lastModified(Path path) {
    return list(path).reduce(RFiles::lastModified);
  }

  public static Flux<Path> list(Path path) {
    return Mono.fromCallable(() -> Files.list(path))
               .flatMapMany(Flux::fromStream)
               .filter(f -> !Files.isDirectory(f))
               .sort();
  }

  public static Flux<RFile> publish(Flux<RFile> files) {
    return files.concatMap(RFiles::write);
  }

  public static Mono<String> read(Path path) {
    return Mono.fromCallable(() -> Files.lines(path))
               .flatMapMany(Flux::fromStream)
               .reduce(JOIN_LINES)
               .defaultIfEmpty("");
  }

  public static Mono<List<RFile>> readDir(Path path) {
    return list(path).concatMap(RFiles::readFn).collectList().map(List::ofAll);
  }

  public static Mono<RFile> readFn(Path p) {
    return read(p).map(s -> new RFile(p, s));
  }

  public static Flux<RFile> subscribe(Path path) {
    return RDirWatch.watch(path).flatMap(file -> read(file).map(content -> new RFile(file, content)));
  }

  public static long toIndex(Path path) {
    return toIndex(path, JSON_FILE_EXT);
  }

  public static long toIndex(Path path, String fileExt) {
    var idxStr = path.getFileName().toString().replace(fileExt, "");
    return Long.parseLong(idxStr);
  }

  public static Path toPath(Path path, long index) {
    return toPath(path, index, JSON_FILE_EXT);
  }

  public static Path toPath(Path path, long index, String fileExt) {
    var fileName = String.format("%019d%s", index, fileExt);
    return path.resolve(fileName);
  }

  public static Mono<RFile> write(RFile file) {
    return Mono.fromCallable(() -> Files.writeString(file.path(), file.content(), CREATE_NEW)).thenReturn(file);
  }

  public static Mono<List<RFile>> write(List<RFile> files) {
    return Mono.fromCallable(() -> writeMany(files)).doOnError(e -> {
      log.error(e.getMessage(), e);
      delete(files.map(RFile::path)).doOnError(RFiles::logSevere).subscribe(RFiles::logDeletion);
    });
  }

  private static String infoIndexZero(Path p) {
    return "Directory %s was empty returning index = zero".formatted(p);
  }

  private static Path lastModified(Path p1, Path p2) {
    return (p1.toFile().lastModified() > p2.toFile().lastModified()) ? p1 : p2;
  }

  private static void logDeletion(Path file) {
    log.error("Fallback after error deleting: {} ", file);
  }

  private static void logSevere(Throwable e) {
    log.error("Severe Error while the fallback deletion", e);
  }

  private static List<RFile> writeMany(List<RFile> files) throws IOException {
    for (RFile file : files) {
      Files.writeString(file.path(), file.content(), CREATE_NEW);
    }
    return files;
  }
}
