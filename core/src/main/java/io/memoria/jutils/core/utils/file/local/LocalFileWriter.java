package io.memoria.jutils.core.utils.file.local;

import io.memoria.jutils.core.utils.file.FileWriter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public record LocalFileWriter(Scheduler scheduler) implements FileWriter {
  @Override
  public Mono<Path> writeFile(Path path, String content) {
    return Mono.fromCallable(() -> Files.writeString(path, content, StandardOpenOption.CREATE))
               .subscribeOn(this.scheduler);
  }
}
