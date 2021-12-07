package io.memoria.reactive.core.file;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BinaryOperator;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class RFiles {
  private static final BinaryOperator<String> JOIN_LINES = (a, b) -> a + System.lineSeparator() + b;

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

  public static Mono<Path> lastModified(Path path) {
    return list(path).reduce(RFiles::lastModified);
  }

  public static Flux<Path> list(Path path) {
    return Mono.fromCallable(() -> Files.list(path))
               .flatMapMany(Flux::fromStream)
               .filter(f -> !Files.isDirectory(f))
               .sort();
  }

  public static Mono<String> read(Path path) {
    return Mono.fromCallable(() -> Files.lines(path))
               .flatMapMany(Flux::fromStream)
               .reduce(JOIN_LINES)
               .defaultIfEmpty("");
  }

  public static Mono<Path> write(Path path, String content) {
    return Mono.fromCallable(() -> Files.writeString(path, content, CREATE_NEW)).thenReturn(path);
  }

  private static Path lastModified(Path p1, Path p2) {
    return (p1.toFile().lastModified() > p2.toFile().lastModified()) ? p1 : p2;
  }
}
