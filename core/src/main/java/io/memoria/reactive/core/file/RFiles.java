package io.memoria.reactive.core.file;

import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BinaryOperator;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class RFiles {

  private static final Logger log = LoggerFactory.getLogger(RFiles.class.getName());
  private static final BinaryOperator<String> JOIN_LINES = (a, b) -> a + System.lineSeparator() + b;

  public static Flux<Path> clean(Path path) {
    return listFiles(path).flatMap(RFiles::delete);
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

  public static Mono<Path> lastModified(Path path) {
    return listFiles(path).reduce(RFiles::lastModified);
  }

  public static Mono<List<Path>> list(Path path) {
    return listFiles(path).collectList().map(List::ofAll);
  }

  public static Flux<Path> listFiles(Path path) {
    return Mono.fromCallable(() -> Files.list(path)).flatMapMany(Flux::fromStream).filter(f -> !Files.isDirectory(f));
  }

  public static Flux<Path> publish(Flux<RFile> files) {
    return files.concatMap(RFiles::write);
  }

  public static Mono<String> read(Path path) {
    return Mono.fromCallable(() -> Files.lines(path))
               .flatMapMany(Flux::fromStream)
               .reduce(JOIN_LINES)
               .defaultIfEmpty("");
  }

  public static Mono<List<RFile>> readDir(Path path) {
    return listFiles(path).flatMap(RFiles::readFn).collectList().map(List::ofAll);
  }

  public static Mono<RFile> readFn(Path p) {
    return read(p).map(s -> new RFile(p, s));
  }

  public static Flux<RFile> subscribe(Path path) {
    return RDirWatch.watch(path).flatMap(file -> read(file).map(content -> new RFile(file, content)));
  }

  public static Mono<Path> write(RFile file) {
    return Mono.fromCallable(() -> Files.writeString(file.path(), file.content(), CREATE_NEW));
  }

  public static Mono<List<Path>> write(List<RFile> files) {
    return Mono.fromCallable(() -> Flux.fromIterable(files).concatMap(RFiles::write))
               .map(Flux::toIterable)
               .map(List::ofAll)
               .doOnError(e -> {
                 log.error(e.getMessage(), e);
                 delete(files.map(RFile::path)).doOnError(RFiles::logSevere).subscribe(RFiles::logDeletion);
               });
  }

  private static Path lastModified(Path p1, Path p2) {
    return (p1.toFile().lastModified() > p2.toFile().lastModified()) ? p1 : p2;
  }

  private static void logDeletion(Path file) {
    log.error("Fallback after error deleting: " + file);
  }

  private static void logSevere(Throwable e) {
    log.error("Severe Error while the fallback deletion:" + e.getMessage(), e);
  }
}
