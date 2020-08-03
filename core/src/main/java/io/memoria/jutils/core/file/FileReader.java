package io.memoria.jutils.core.file;

import io.memoria.jutils.core.yaml.Yaml;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public interface FileReader {
  static Try<Path> resourcePath(String path) {
    return Try.of(() -> {
      var url = ClassLoader.getSystemClassLoader().getResource(path);
      return Paths.get(Objects.requireNonNull(url).getPath());
    });
  }

  Mono<String> file(Path path);

  Flux<String> lines(Path path);

  Mono<Yaml> yaml(Path path);
}
