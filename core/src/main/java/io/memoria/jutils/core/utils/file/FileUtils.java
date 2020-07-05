package io.memoria.jutils.core.utils.file;

import io.memoria.jutils.core.utils.functional.ReactorVavrUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.Objects.requireNonNull;

public class FileUtils {
  public static Mono<String> file(String fileName) {
    return fileLines(fileName).reduce((a, b) -> a + "\n" + b);
  }

  public static Flux<String> fileLines(String fileName) {
    try {
      return Flux.fromStream(Files.lines(Path.of(fileName)));
    } catch (IOException e) {
      return Flux.error(e);
    }
  }

  public static Mono<String> resource(String fileName) {
    return resourceLines(fileName).reduce((a, b) -> a + "\n" + b);
  }

  public static Flux<String> resourceLines(String fileName) {
    try {
      var p = Paths.get(requireNonNull(ClassLoader.getSystemClassLoader().getResource(fileName)).toURI());
      return Flux.fromStream(Files.lines(p));
    } catch (URISyntaxException | IOException e) {
      return Flux.error(e);
    }
  }

  public static Mono<Path> writeFile(String path, String content, Scheduler scheduler) {
    return writeFile(path, content, scheduler, CREATE);
  }

  public static Mono<Path> writeFile(String path, String content, Scheduler scheduler, StandardOpenOption... options) {
    return ReactorVavrUtils.toMono(() -> Files.writeString(Paths.get(path), content, options), scheduler);
  }

  private FileUtils() {}
}
