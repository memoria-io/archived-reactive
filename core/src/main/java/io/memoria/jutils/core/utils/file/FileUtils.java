package io.memoria.jutils.core.utils.file;

import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.BaseStream;

public class FileUtils {
  private static final BiFunction<String, String, String> joinLines = (a, b) -> a + System.lineSeparator() + b;
  private final Scheduler scheduler;

  private FileUtils(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public static FileUtils build() {
    return new FileUtils(Schedulers.boundedElastic());
  }

  public static FileUtils build(Scheduler scheduler) {
    return new FileUtils(scheduler);
  }

  public static Try<Path> resourcePath(String path) {
    return Try.of(() -> Paths.get(ClassLoader.getSystemResource(path).toURI()));
  }

  public Mono<String> read(Path filePath) {
    return readLines(filePath).reduce(joinLines);
  }

  public Mono<String> read(Path filePath, String nestingPrefix) {
    return readLines(filePath, nestingPrefix).reduce(joinLines);
  }

  public Flux<String> readLines(Path filePath) {
    return Flux.using(() -> Files.lines(filePath), Flux::fromStream, BaseStream::close).subscribeOn(scheduler);
  }

  public Flux<String> readLines(Path filePath, String nestingPrefix) {
    return readLines(filePath).concatMap(line -> expand(filePath, line, nestingPrefix));
  }

  public Mono<String> readResource(String resourcePath) {
    return Mono.fromCallable(() -> {
      var inputStream = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(resourcePath));
      var result = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) != -1) {
        result.write(buffer, 0, length);
      }
      return result.toString(StandardCharsets.UTF_8);
    }).subscribeOn(scheduler);
  }

  public Mono<String> readResource(String resourcePath, String nestingPrefix) {
    return readResourceLines(resourcePath, nestingPrefix).reduce(joinLines);
  }

  public Flux<String> readResourceLines(String resourcePath) {
    return readResource(resourcePath).map(String::lines).flatMapMany(Flux::fromStream);
  }

  public Flux<String> readResourceLines(String resourcePath, String nestingPrefix) {
    return readResourceLines(resourcePath).concatMap(line -> expandResource(resourcePath, line, nestingPrefix));
  }

  public Mono<Path> write(Path path, String content) {
    return Mono.fromCallable(() -> Files.writeString(path, content, StandardOpenOption.CREATE)).subscribeOn(scheduler);
  }

  private Flux<String> expand(Path filePath, String line, String nestingPrefix) {
    if (line.trim().startsWith(nestingPrefix)) {
      var subFilePath = line.split(nestingPrefix)[1].trim();
      if (subFilePath.startsWith("/")) {
        return readLines(Paths.get(subFilePath), nestingPrefix);
      } else {
        // Handling relative path 
        if (filePath.getParent() != null) {
          var inclusionPath = filePath.getParent().resolve(subFilePath);
          return readLines(inclusionPath, nestingPrefix);
        } else {
          var inclusionPath = filePath.resolve(subFilePath);
          return readLines(inclusionPath, nestingPrefix);
        }
      }
    } else {
      return Flux.just(line);
    }
  }

  private Flux<String> expandResource(String filePath, String line, String nestingPrefix) {
    if (line.trim().startsWith(nestingPrefix)) {
      var subFilePath = line.split(nestingPrefix)[1].trim();
      var relativePath = parentPath(filePath) + subFilePath;
      return readResourceLines(relativePath, nestingPrefix);
    } else {
      return Flux.just(line);
    }
  }

  private String parentPath(String filePath) {
    return filePath.replaceFirst("[^/]+$", "");
  }
}
