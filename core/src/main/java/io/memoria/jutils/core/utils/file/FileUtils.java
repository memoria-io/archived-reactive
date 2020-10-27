package io.memoria.jutils.core.utils.file;

import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.BaseStream;

public record FileUtils(Scheduler scheduler) {
  private static final BiFunction<String, String, String> joinLines = (a, b) -> a + System.lineSeparator() + b;

  public static Try<Path> resourcePath(String path) {
    return Try.of(() -> {
      var url = ClassLoader.getSystemClassLoader().getResource(path);
      return Paths.get(Objects.requireNonNull(url).getPath());
    });
  }

  public Mono<String> read(Path path) {
    return Mono.fromCallable(() -> Files.readString(path)).subscribeOn(scheduler);
  }

  public Mono<String> read(Path path, String nestingPrefix) {
    return readLines(path, nestingPrefix).reduce(joinLines);
  }

  public Flux<String> readLines(Path path) {
    return Flux.using(() -> Files.lines(path), Flux::fromStream, BaseStream::close).subscribeOn(scheduler);
  }

  public Flux<String> readLines(Path path, String nestingPrefix) {
    return Flux.using(() -> Files.lines(path), Flux::fromStream, BaseStream::close).flatMap(line -> {
      if (line.startsWith(nestingPrefix)) {
        var inclusionPathStr = line.split(nestingPrefix)[1].trim();
        var inclusionPath = path.getParent().resolve(inclusionPathStr);
        return Mono.fromCallable(() -> Files.lines(inclusionPath)).flatMapMany(Flux::fromStream);
      } else {
        return Flux.just(line);
      }
    }).subscribeOn(scheduler);
  }

  public Mono<String> readResource(String path) {
    return resourcePath(path).map(this::read).getOrElseGet(Mono::error);
  }

  public Mono<String> readResource(String path, String nestingPrefix) {
    return resourcePath(path).map(p -> read(p, nestingPrefix)).getOrElseGet(Mono::error);
  }

  public Flux<String> readResourceLines(String path, String nestingPrefix) {
    return resourcePath(path).map(p -> readLines(p, nestingPrefix)).getOrElseGet(Flux::error);
  }

  public Flux<String> readResourceLines(String path) {
    return resourcePath(path).map(this::readLines).getOrElseGet(Flux::error);
  }

  public Mono<Path> write(Path path, String content) {
    return Mono.fromCallable(() -> Files.writeString(path, content, StandardOpenOption.CREATE)).subscribeOn(scheduler);
  }
}
