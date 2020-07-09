package io.memoria.jutils.core.utils.file;

import reactor.core.publisher.Mono;

import java.nio.file.Path;

public interface ReactiveFileWriter {
  Mono<Path> writeFile(Path path, String content);
}
