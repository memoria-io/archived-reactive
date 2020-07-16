package io.memoria.jutils.core.utils.file;

import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface FileReader {
  static Try<Path> resourcePath(String path) {
    var url = ClassLoader.getSystemClassLoader().getResource(path);
    return (url != null) ? Try.of(() -> Paths.get(url.getPath())) : Try.failure(new NullPointerException());
  }

  Mono<String> file(Path path);

  Flux<String> lines(Path path);

  Mono<YamlConfigMap> yaml(Path path);
}
