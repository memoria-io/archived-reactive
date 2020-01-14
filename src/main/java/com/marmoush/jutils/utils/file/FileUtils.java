package com.marmoush.jutils.utils.file;

import io.vavr.collection.List;
import io.vavr.control.Try;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.marmoush.jutils.utils.functional.ReactorVavrUtils.blockingToMono;
import static java.nio.file.StandardOpenOption.CREATE;

public class FileUtils {
  private FileUtils() {}

  public static Try<String> resource(String fileName) {
    return resourceLines(fileName).map(l -> String.join("\n", l));
  }

  public static Try<String> file(String fileName) {
    return fileLines(fileName).map(l -> String.join("\n", l));
  }

  public static Try<List<String>> resourceLines(String fileName) {
    return Try.of(() -> List.ofAll(Files.readAllLines(Paths.get(ClassLoader.getSystemClassLoader()
                                                                           .getResource(fileName)
                                                                           .toURI()))));
  }

  public static Try<List<String>> fileLines(String fileName) {
    return Try.of(() -> List.ofAll(Files.readAllLines(Path.of(fileName))));
  }

  public static Mono<Try<Path>> writeFile(String path, String content, Scheduler scheduler) {
    return writeFile(path, content, scheduler, CREATE);
  }

  public static Mono<Try<Path>> writeFile(String path,
                                          String content,
                                          Scheduler scheduler,
                                          StandardOpenOption... options) {
    return blockingToMono(() -> Try.of(() -> Files.write(Paths.get(path), content.getBytes(), options)), scheduler);
  }
}
