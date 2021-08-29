package io.memoria.reactive.core.file;

import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.BinaryOperator;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class RFiles {

  public static final BinaryOperator<String> JOIN_LINES = (a, b) -> a + System.lineSeparator() + b;
  private static final String SEP = File.separator;
  private static final Logger log = LoggerFactory.getLogger(RFiles.class.getName());

  public static Flux<Boolean> clean(Path path) {
    return Mono.fromCallable(path::toFile)
               .map(File::listFiles)
               .map(Arrays::asList)
               .flatMapMany(Flux::fromIterable)
               .filter(f -> !f.isDirectory())
               .map(File::delete);
  }

  public static Mono<Path> delete(Path path) {
    return Mono.fromCallable(() -> Files.deleteIfExists(path)).thenReturn(path);
  }

  public static Mono<Set<Path>> delete(Set<Path> files) {
    return Flux.fromIterable(files).flatMap(RFiles::delete).collectList().map(HashSet::ofAll);
  }

  public static Mono<Path> lastFile(Path directoryPath) {
    return Mono.fromCallable(() -> Files.list(directoryPath))
               .flatMapMany(Flux::fromStream)
               .filter(f -> !Files.isDirectory(f))
               .reduce(RFiles::lastModified);
  }

  public static Mono<Path> overwrite(Path path, String content) {
    return Mono.fromCallable(() -> Files.writeString(path, content, TRUNCATE_EXISTING, WRITE)).thenReturn(path);
  }

  public static Flux<Path> publish(Flux<Tuple2<Path, String>> files) {
    return files.concatMap(t -> write(t._1, t._2));
  }

  public static Mono<String> read(String path) {
    return Mono.fromCallable(() -> Files.lines(Path.of(path))).flatMapMany(Flux::fromStream).reduce(JOIN_LINES);
  }

  public static Mono<LinkedHashMap<String, String>> readDirectory(String path) {
    return Flux.using(() -> readDirectoryStream(path), Flux::fromIterable, ReactorVavrUtils::closeReader)
               .flatMap(p -> read(p.toString()).map(content -> Tuple.of(p.getFileName().toString(), content)))
               .reduce(LinkedHashMap.empty(), LinkedHashMap::put);
  }

  public static DirectoryStream<Path> readDirectoryStream(String path) throws IOException {
    return Files.newDirectoryStream(Path.of(path));
  }

  public static Flux<Tuple2<String, String>> subscribe(String path, long offset) {
    var existingFiles = readDirectory(path).flatMapMany(Flux::fromIterable);
    var newFiles = RDirWatch.watch(path).flatMap(file -> read(file).map(content -> Tuple.of(file, content)));
    return Flux.concat(existingFiles, newFiles).skip(offset);
  }

  public static Mono<Path> write(Path path, String content) {
    return Mono.fromCallable(() -> Files.writeString(path, content, CREATE_NEW)).thenReturn(path);
  }

  public static Mono<List<Path>> write(LinkedHashMap<Path, String> files) {
    return Mono.fromCallable(() -> Flux.fromIterable(files).concatMap(t -> write(t._1, t._2)))
               .map(Flux::toIterable)
               .map(List::ofAll)
               .doOnError(e -> {
                 log.error(e.getMessage(), e);
                 delete(files.keySet()).doOnError(RFiles::logSevere).subscribe(RFiles::logDeletion);
               });
  }

  static Path lastModified(Path p1, Path p2) {
    return (p1.toFile().lastModified() > p2.toFile().lastModified()) ? p1 : p2;
  }

  static void logDeletion(Set<Path> path) {
    log.error("Fallback after error deleting: " + path);
  }

  static void logSevere(Throwable e) {
    log.error("Error while deletion:" + e.getMessage(), e);
  }
}
