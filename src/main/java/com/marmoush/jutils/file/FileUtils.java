package com.marmoush.jutils.file;

import io.vavr.control.Try;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static com.marmoush.jutils.functional.Functional.blockingToMono;
import static java.nio.file.StandardOpenOption.CREATE;

public class FileUtils {
  private FileUtils() {}

  public static InputStream resource(String fileName) {
    return ClassLoader.getSystemClassLoader().getResourceAsStream(fileName);
  }

  public static Try<InputStream> file(String fileName, OpenOption... openOptions) {
    return Try.of(() -> Files.newInputStream(Path.of(fileName), openOptions));
  }

  public static Try<File> asFile(String fileName) {
    return Try.of(() -> new File(ClassLoader.getSystemClassLoader().getResource(fileName).toURI()));
  }

  public static Mono<String> asString(InputStream fileName, Scheduler scheduler) {
    return blockingToMono(() -> asStringBlocking(fileName), scheduler);
  }

  public static Mono<String> asString(InputStream fileName, UnaryOperator<String> lineFunc, Scheduler scheduler) {
    return blockingToMono(() -> asStringBlocking(fileName, lineFunc), scheduler);
  }

  public static String asStringBlocking(InputStream fileName) {
    return asStringBlocking(fileName, UnaryOperator.identity());
  }

  public static String asStringBlocking(InputStream is, UnaryOperator<String> lineFunc) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    return reader.lines().map(lineFunc).collect(Collectors.joining(System.lineSeparator()));
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
