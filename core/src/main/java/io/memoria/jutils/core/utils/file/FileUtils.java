package io.memoria.jutils.core.utils.file;

import io.memoria.jutils.core.transformer.Properties;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;

public class FileUtils {
  public static Function<String, Flux<String>> resolveNested(Path current, String prefix) {
    return (line) -> {
      if (line.startsWith(prefix)) {
        String inclusionPath = line.split(prefix)[1].trim();
        return readLines(current.getParent().resolve(inclusionPath));
      } else {
        return Flux.just(line);
      }
    };
  }

  public static Mono<String> read(Scheduler scheduler, Path path) {
    return Mono.fromCallable(() -> Files.readString(path)).subscribeOn(scheduler);
  }

  public static Mono<String> read(Path path) {
    return read(Schedulers.elastic(), path);
  }

  public static Flux<String> readLines(Scheduler scheduler, Path path) {
    try {
      return Flux.fromStream(Files.lines(path)).subscribeOn(scheduler);
    } catch (IOException e) {
      return Flux.error(e);
    }
  }

  public static Flux<String> readLines(Path path) {
    return readLines(Schedulers.elastic(), path);
  }
public static Mono<Properties>
  public static Mono<Path> write(Path path, String content, Scheduler scheduler) {
    return Mono.fromCallable(() -> Files.writeString(path, content, StandardOpenOption.CREATE)).subscribeOn(scheduler);
  }

  public static Mono<Path> write(Path path, String content) {
    return write(path, content, Schedulers.elastic());
  }

  private FileUtils() {}
}
