package io.memoria.jutils.jcore.file;

import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.Serializable;
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

  <T extends Serializable> Mono<T> deserialize(String path, Class<T> tClass);

  Mono<String> read(String resourcePath);

  Flux<String> readLines(String resourcePath);

  <T extends Serializable> Mono<T> serialize(Path path, T t);

  Mono<Path> write(Path path, String content);
}
