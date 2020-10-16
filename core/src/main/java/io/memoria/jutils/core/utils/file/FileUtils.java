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

public record FileUtils(Scheduler scheduler) {
  private static final BiFunction<String, String, String> joinLines = (a, b) -> a + "\n" + b;

  public static Try<Path> resourcePath(String path) {
    return Try.of(() -> {
      var url = ClassLoader.getSystemClassLoader().getResource(path);
      return Paths.get(Objects.requireNonNull(url).getPath());
    });
  }

  public Mono<String> read(Path path) {
    return readLines(path).reduce(joinLines);
  }

  public Mono<String> read(Path path, String nestingPrefix) {
    return readLines(path, nestingPrefix).reduce(joinLines);
  }

  public Flux<String> readLines(Path path) {
    return Mono.fromCallable(() -> Files.lines(path)).flatMapMany(Flux::fromStream).subscribeOn(scheduler);
  }

  public Flux<String> readLines(Path path, String nestingPrefix) {
    return Mono.fromCallable(() -> Files.lines(path)).flatMapMany(Flux::fromStream).flatMap(line -> {
      if (line.startsWith(nestingPrefix)) {
        var inclusionPathStr = line.split(nestingPrefix)[1].trim();
        var inclusionPath = path.getParent().resolve(inclusionPathStr);
        System.out.println(inclusionPath);
        return Mono.fromCallable(() -> Files.lines(inclusionPath)).flatMapMany(Flux::fromStream);
      } else {
        return Flux.just(line);
      }
    }).subscribeOn(scheduler);
  }

  public Mono<String> readResource(String path) {
    return readResourceLines(path).reduce(joinLines);
  }

  public Mono<String> readResource(String path, String nestingPrefix) {
    return readResourceLines(path, nestingPrefix).reduce(joinLines);
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
