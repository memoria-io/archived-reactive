package io.memoria.jutils.core.utils.yaml;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import io.memoria.jutils.core.utils.file.FileUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.function.Function;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.checkedMono;

public class YamlUtils {
  private YamlUtils() {}

  /**
   * @param filename      path of the file
   * @param ignoreUnknown ignore extra values in when parsing
   * @return Try of class type T
   */
  public static Mono<YamlConfigMap> parseYamlFile(String filename, boolean ignoreUnknown) {
    return parseYamlFile(MapInstance.class, filename, ignoreUnknown).map(YamlConfigMap::new);
  }

  /**
   * @param t             Class type
   * @param filename      absolute path of the file or project relevant path
   * @param ignoreUnknown ignore extra values in when parsing
   * @param <T>           Type param
   * @return Try of class type T
   */
  public static <T> Mono<T> parseYamlFile(Class<T> t, String filename, boolean ignoreUnknown) {
    return parseYaml(t, filename, FileUtils::fileLines, ignoreUnknown);
  }

  private static <T> Mono<T> parseYaml(Class<T> t,
                                       String fileName,
                                       Function<String, Flux<String>> fileReader,
                                       boolean ignoreUnknown) {
    YamlConfig yc = new YamlConfig();
    yc.readConfig.setIgnoreUnknownProperties(ignoreUnknown);
    return fileReader.apply(fileName)
                     .flatMap(f -> yamlInclude(f, fileReader))
                     .reduce((a, b) -> a + "\n" + b)
                     .flatMap(s -> checkedMono(() -> new YamlReader(s, yc).read(t)));
  }

  private static Flux<String> yamlInclude(String line, Function<String, Flux<String>> reader) {
    if (line.startsWith("include:")) {
      String file = line.split(":")[1].trim();
      return reader.apply(file);
    } else {
      return Flux.just(line);
    }
  }

  /**
   * @param filename path of the file under e.g resources/filename
   * @return Try of class type T
   */
  public static Mono<YamlConfigMap> parseYamlResource(String filename) {
    return parseYamlResource(MapInstance.class, filename, false).map(YamlConfigMap::new);
  }

  /**
   * @param t             Class type
   * @param filename      path of the file under e.g resources/filename
   * @param ignoreUnknown ignore extra values in when parsing
   * @param <T>           Type param
   * @return Try of class type T
   */
  public static <T> Mono<T> parseYamlResource(Class<T> t, String filename, boolean ignoreUnknown) {
    return parseYaml(t, filename, FileUtils::resourceLines, ignoreUnknown);
  }

  private static final class MapInstance extends HashMap<String, Object> {}
}

