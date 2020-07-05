package io.memoria.jutils.core.utils.yaml;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import io.memoria.jutils.core.utils.file.FileUtils;
import io.vavr.Function1;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.HashMap;
import java.util.function.Function;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toMono;

public class YamlUtils {
  private static final class MapInstance extends HashMap<String, Object> {}

  public static <T> Mono<T> parseYamlFile(Class<T> t, String filename, boolean ignoreUnknown, Scheduler scheduler) {
    return parseYaml(t, filename, FileUtils::fileLines, ignoreUnknown, scheduler);
  }

  public static Function1<String, Mono<YamlConfigMap>> parseYamlFile(Scheduler scheduler) {
    return filename -> parseYamlFile(MapInstance.class, filename, true, scheduler).map(YamlConfigMap::new);
  }

  public static Function1<String, Mono<YamlConfigMap>> parseYamlResource(Scheduler scheduler) {
    return fileName -> parseYamlResource(MapInstance.class, fileName, true, scheduler).map(YamlConfigMap::new);
  }

  public static <T> Mono<T> parseYamlResource(Class<T> t, String filename, boolean ignoreUnknown, Scheduler scheduler) {
    return parseYaml(t, filename, FileUtils::resourceLines, ignoreUnknown, scheduler);
  }

  private static <T> Mono<T> parseYaml(Class<T> t,
                                       String fileName,
                                       Function<String, Flux<String>> fileReader,
                                       boolean ignoreUnknown,
                                       Scheduler scheduler) {
    YamlConfig yc = new YamlConfig();
    yc.readConfig.setIgnoreUnknownProperties(ignoreUnknown);
    return fileReader.apply(fileName)
                     .flatMap(f -> yamlInclude(f, fileReader))
                     .reduce((a, b) -> a + "\n" + b)
                     .map(s -> new YamlReader(s, yc))
                     .flatMap(yamlReader -> toMono(() -> yamlReader.read(t), scheduler));
  }

  private static Flux<String> yamlInclude(String line, Function<String, Flux<String>> reader) {
    if (line.startsWith("include:")) {
      String file = line.split(":")[1].trim();
      return reader.apply(file);
    } else {
      return Flux.just(line);
    }
  }

  private YamlUtils() {}
}

