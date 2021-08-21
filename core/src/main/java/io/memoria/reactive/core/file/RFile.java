package io.memoria.reactive.core.file;

import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;
import java.util.function.BinaryOperator;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.util.function.Function.identity;

public class RFile {

  public static final BinaryOperator<String> JOIN_LINES = (a, b) -> a + System.lineSeparator() + b;
  private static final Logger log = LoggerFactory.getLogger(RFile.class.getName());

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

  public static String parentPath(String filePath) {
    return filePath.replaceFirst("[^/]+$", ""); //NOSONAR
  }

  public static Mono<String> read(String path) {
    return readLines(path).reduce(JOIN_LINES);
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

  public static Flux<String> subscribe(String path, long offset) {
    var files = readDirectory(path).flatMapMany(Flux::fromIterable);
    return Flux.concat(files, watch(path)).skip(offset);
  }

  public static Mono<String> write(String path, String content) {
    return Mono.fromCallable(() -> Files.writeString(Path.of(path), content, CREATE)).thenReturn(content);
  }

  public static Flux<String> publish(String directory, Flux<Tuple2<String, String>> files) {
    return files.concatMap(t -> write(directory, t._1, t._2));
  }

  private static Flux<String> write(String directory, LinkedHashMap<String, String> files) {
    return Flux.fromIterable(files)
               .concatMap(t -> write(directory, t._1, t._2))
               .doOnError(e -> delete(directory, files.keySet()));
  }

  public static Flux<String> delete(String directory, Set<String> files) {
    return Flux.fromIterable(files).concatMap(f -> delete(directory, f));
  }

  public static Mono<String> delete(String directory, String fileName) {
    return Mono.fromCallable(() -> Files.deleteIfExists(Path.of(directory, fileName))).thenReturn(fileName);
  }

  private static Mono<String> write(String directory, String fileName, String content) {
    return Mono.fromCallable(() -> Files.writeString(Path.of(directory, fileName), content, CREATE))
               .thenReturn(fileName);
  }

  public static Mono<List<String>> readDirectory(String path) {
    return Mono.fromCallable(() -> new File(path).listFiles())
               .flatMapMany(Flux::fromArray)
               .map(File::getName)
               .collectList()
               .map(List::ofAll);
  }

  private static Flux<String> take(WatchService watchService) {
    return Mono.fromCallable(() -> {
      WatchKey key = watchService.take();
      var l = List.ofAll(key.pollEvents()).map(WatchEvent::context).map(Object::toString);
      key.reset();
      return l;
    }).flatMapMany(Flux::fromIterable);
  }

  private static Flux<String> watch(String dir) {
    return Mono.fromCallable(() -> watchService(Path.of(dir))).flatMapMany(RFile::watch);
  }

  private static Flux<String> watch(WatchService ws) {
    return Flux.generate((SynchronousSink<Flux<String>> s) -> s.next(take(ws))).concatMap(identity());
  }

  private static WatchService watchService(Path dir) throws IOException {
    WatchService watchService = FileSystems.getDefault().newWatchService();
    dir.register(watchService, ENTRY_CREATE);
    return watchService;
  }
}
