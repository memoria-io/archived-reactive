package io.memoria.reactive.core.file;

import io.memoria.reactive.core.vavr.ReactorVavrUtils;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BinaryOperator;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class RFile {

  public static final BinaryOperator<String> JOIN_LINES = (a, b) -> a + System.lineSeparator() + b;
  private static final Logger log = LoggerFactory.getLogger(RFile.class.getName());

  public static Flux<Boolean> clean(String directory) {
    return Mono.fromCallable(() -> Path.of(directory))
               .map(Path::toFile)
               .map(File::listFiles)
               .map(Arrays::asList)
               .flatMapMany(Flux::fromIterable)
               .filter(f -> !f.isDirectory())
               .map(File::delete);
  }

  public static Flux<String> delete(String directory, Set<String> files) {
    return Flux.fromIterable(files).concatMap(f -> delete(directory, f));
  }

  public static Mono<String> delete(String directory, String fileName) {
    return Mono.fromCallable(() -> Files.deleteIfExists(Path.of(directory, fileName))).thenReturn(fileName);
  }

  /**
   * Convenient method to treat path as a resource path if it's not absolute
   */
  public static Try<InputStream> inputStream(String path) {
    if (path.startsWith("/")) {
      return Try.of(() -> Files.newInputStream(Path.of(path)));
    } else {
      return Try.of(() -> ClassLoader.getSystemResourceAsStream(path));
    }
  }

  public static String join(String path, String... paths) {
    var sep = "/";
    var ps = String.join(sep, paths);
    return (path.endsWith(sep)) ? path + ps : path + sep + ps;
  }

  public static Mono<Path> lastFile(String directory) {
    return Mono.fromCallable(() -> Files.list(Path.of(directory)))
               .flatMapMany(Flux::fromStream)
               .filter(f -> !Files.isDirectory(f))
               .reduce(RFile::lastModified);
  }

  public static Mono<String> lastFileName(String directory) {
    return lastFile(directory).map(Path::getFileName).map(Objects::toString);
  }

  public static Mono<String> overwrite(String path, String content) {
    return Mono.fromCallable(() -> Files.writeString(Path.of(path), content, TRUNCATE_EXISTING, WRITE))
               .thenReturn(content);
  }

  public static String parentPath(String filePath) {
    return filePath.replaceFirst("[^/]+$", ""); //NOSONAR
  }

  public static Flux<String> publish(String directory, Flux<Tuple2<String, String>> files) {
    return files.concatMap(t -> write(directory, t._1, t._2));
  }

  public static Mono<String> read(String path) {
    return readLines(path).reduce(JOIN_LINES);
  }

  public static Mono<LinkedHashMap<String, String>> readDirectory(String path) {
    return Flux.using(() -> RFileUtils.readDirectoryStream(path), Flux::fromIterable, ReactorVavrUtils::closeReader)
               .flatMap(p -> read(p.toString()).map(content -> Tuple.of(p.getFileName().toString(), content)))
               .reduce(LinkedHashMap.empty(), LinkedHashMap::put);
  }

  /**
   * if path starts with slash "/" file is read using path, else it's read as resource
   */
  public static Flux<String> readLines(String path) {
    if (path.startsWith("/")) {
      return Mono.fromCallable(() -> Files.lines(Path.of(path))).flatMapMany(Flux::fromStream);
    } else {
      return Mono.fromCallable(() -> readResource(path)).flatMapMany(Flux::fromIterable);
    }
  }

  public static List<String> readResource(String path) throws IOException {
    try (var inputStream = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(path))) {
      var result = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) != -1) {
        result.write(buffer, 0, length);
      }
      var file = result.toString(StandardCharsets.UTF_8);
      return List.of(file.split("\\r?\\n"));
    }
  }

  public static Flux<Tuple2<String, String>> subscribe(String path, long offset) {
    var existingFiles = readDirectory(path).flatMapMany(Flux::fromIterable);
    var newFiles = RFileUtils.watch(path).flatMap(file -> read(file).map(content -> Tuple.of(file, content)));
    return Flux.concat(existingFiles, newFiles).skip(offset);
  }

  public static Mono<String> write(String path, String content) {
    return Mono.fromCallable(() -> Files.writeString(Path.of(path), content, WRITE)).thenReturn(content);
  }

  public static Mono<List<String>> write(String path, LinkedHashMap<String, String> files) {
    return Mono.fromCallable(() -> Flux.fromIterable(files).concatMap(t -> write(join(path, t._1), t._2)))
               .map(Flux::toIterable)
               .map(List::ofAll)
               .doOnError(e -> {
                 log.error(e.getMessage(), e);
                 delete(path, files.keySet()).doOnError(RFile::logSevere).subscribe(f -> logDeletion(path, f));
               });
  }

  private static Path lastModified(Path p1, Path p2) {
    return (p1.toFile().lastModified() > p2.toFile().lastModified()) ? p1 : p2;
  }

  private static void logDeletion(String path, String file) {
    log.error("Fallback after error deleting: " + join(path, file));
  }

  private static void logSevere(Throwable e) {
    log.error("Error while deletion:" + e.getMessage(), e);
  }

  private static Mono<String> write(String directory, String fileName, String content) {
    return Mono.fromCallable(() -> Files.writeString(Path.of(directory, fileName), content, CREATE))
               .thenReturn(fileName);
  }
}
