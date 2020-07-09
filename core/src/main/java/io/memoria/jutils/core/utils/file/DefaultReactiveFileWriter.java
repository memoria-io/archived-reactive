package io.memoria.jutils.core.utils.file;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public record DefaultReactiveFileWriter(Scheduler scheduler) implements ReactiveFileWriter {
  @Override
  public Mono<Path> writeFile(Path path, String content) {
    return Mono.fromCallable(() -> Files.writeString(path, content, StandardOpenOption.CREATE))
               .subscribeOn(this.scheduler);
  }
}
