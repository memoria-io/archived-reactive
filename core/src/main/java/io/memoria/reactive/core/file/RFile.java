package io.memoria.reactive.core.file;

import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.BinaryOperator;

public class RFile {

  public static final BinaryOperator<String> JOIN_LINES = (a, b) -> a + System.lineSeparator() + b;

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

  public static Mono<Path> write(Path path, String content) {
    return Mono.fromCallable(() -> Files.writeString(path, content, StandardOpenOption.CREATE));
  }
}
