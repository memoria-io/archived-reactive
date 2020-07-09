package io.memoria.jutils.core.utils.file;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public record DefaultReactiveFileReader(Scheduler scheduler) implements ReactiveFileReader {
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

  public Mono<YamlConfigMap> yaml(Path path) {
    YamlConfig yc = new YamlConfig();
    yc.readConfig.setIgnoreUnknownProperties(true);
    return lines(path).flatMap(l -> yamlInclude(l, path))
                      .reduce((a, b) -> a + "\n" + b)
                      .map(s -> new YamlReader(s, yc))
                      .flatMap(yamlReader -> Mono.fromCallable(() -> yamlReader.read(MapInstance.class)))
                      .map(YamlConfigMap::new);
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
