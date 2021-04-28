package io.memoria.jutils.jcore.file;

import io.vavr.control.Option;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.File;
import java.nio.file.Path;

public interface FileUtils {
  /**
   * @param nestingPrefix
   * @param resolveSystemEnv when true, any line which contains ${ENV_VALUE:-defaultValue} will be resolved from system
   *                         environment
   * @param scheduler
   * @return default instance implementing FileUtils
   */
  static FileUtils createDefault(Option<String> nestingPrefix, boolean resolveSystemEnv, Scheduler scheduler) {
    return new DefaultFileUtils(nestingPrefix, resolveSystemEnv, scheduler);
  }

  /**
   * Convenient method to treat path as a resource path if it's not absolute
   *
   * @param path
   * @return a try of file
   */
  static Try<File> file(String path) {
    if (path.startsWith("/")) {
      return Try.success(new File(path));
    } else {
      return Try.of(() -> new File(ClassLoader.getSystemResource(path).toURI()));
    }
  }

  Mono<String> read(String resourcePath);

  Flux<String> readLines(String resourcePath);

  Mono<Path> write(Path path, String content);
}
