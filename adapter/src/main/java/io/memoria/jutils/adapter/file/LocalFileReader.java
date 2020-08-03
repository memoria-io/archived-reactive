package io.memoria.jutils.adapter.file;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import io.memoria.jutils.adapter.yaml.YamlMap;
import io.memoria.jutils.core.file.FileReader;
import io.memoria.jutils.core.yaml.Yaml;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public record LocalFileReader(Scheduler scheduler) implements FileReader {
  private static final class MapInstance extends HashMap<String, Object> {}

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

  public Mono<Yaml> yaml(Path path) {
    return lines(path).flatMap(l -> yamlInclude(l, path))
                      .reduce((a, b) -> a + "\n" + b)
                      .map(LocalFileReader::yamlReader)
                      .flatMap(yamlReader -> Mono.fromCallable(() -> yamlReader.read(MapInstance.class)))
                      .map(YamlMap::new);
  }

  private Flux<String> yamlInclude(String line, Path relativePath) {
    if (line.startsWith("include:")) {
      String path = line.split(":")[1].trim();
      return lines(relativePath.getParent().resolve(path));
    } else {
      return Flux.just(line);
    }
  }

  private static YamlReader yamlReader(String s) {
    YamlConfig yc = new YamlConfig();
    yc.readConfig.setIgnoreUnknownProperties(true);
    return new YamlReader(s, yc);
  }
}
