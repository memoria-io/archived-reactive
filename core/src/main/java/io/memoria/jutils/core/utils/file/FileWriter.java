package io.memoria.jutils.core.utils.file;

import reactor.core.publisher.Mono;

import java.nio.file.Path;

public interface FileWriter {
  Mono<Path> writeFile(Path path, String content);
}
