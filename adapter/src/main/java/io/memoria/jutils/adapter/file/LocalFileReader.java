package io.memoria.jutils.adapter.file;

import io.memoria.jutils.adapter.yaml.YamlBeans;
import io.memoria.jutils.adapter.yaml.YamlProperties;
import io.memoria.jutils.core.Properties;
import io.memoria.jutils.core.file.FileReader;
import io.memoria.jutils.core.yaml.Yaml;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toMono;

public record LocalFileReader(Scheduler scheduler) implements FileReader {

  @Override
  public Mono<String> file(Path path) {
    return lines(path).reduce((a, b) -> a + "\n" + b);
  }

  @Override
  public Flux<String> lines(Path path) {
    try {
      return Flux.fromStream(Files.lines(path)).subscribeOn(scheduler);
    } catch (Exception e) {
      return Flux.error(e);
    }
  }

  public Mono<Properties> yaml(Path path) {
    final class MapInstance extends HashMap<String, Object> {}
    Yaml yaml = new YamlBeans(true);
    return lines(path).flatMap(l -> yamlInclude(l, path))
                      .reduce((a, b) -> a + "\n" + b)
                      .flatMap(str -> toMono(yaml.deserialize(str, MapInstance.class)))
                      .map(YamlProperties::new);
  }

  private Flux<String> yamlInclude(String line, Path relativePath) {
    if (line.startsWith("include:")) {
      String path = line.split(":")[1].trim();
      return lines(relativePath.getParent().resolve(path));
    } else {
      return Flux.just(line);
    }
  }
}
