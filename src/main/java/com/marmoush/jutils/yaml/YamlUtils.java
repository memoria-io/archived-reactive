package com.marmoush.jutils.yaml;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.marmoush.jutils.file.FileUtils;
import io.vavr.collection.List;
import io.vavr.control.Try;

import java.util.HashMap;
import java.util.function.Function;

public class YamlUtils {
  private YamlUtils() {}

  public static class ConfigMap extends HashMap<String, Object> {}

  /**
   * @param filename      path of the file under e.g resources/filename
   * @param ignoreUnknown ignore extra values in when parsing
   * @return Try of class type T
   */
  public static Try<ConfigMap> parseYamlFile(String filename, boolean ignoreUnknown) {
    return parseYamlFile(ConfigMap.class, filename, ignoreUnknown);
  }

  /**
   * @param t             Class type
   * @param filename      absolute path of the file or project relevant path
   * @param ignoreUnknown ignore extra values in when parsing
   * @param <T>           Type param
   * @return Try of class type T
   */
  public static <T> Try<T> parseYamlFile(Class<T> t, String filename, boolean ignoreUnknown) {
    return parseYaml(t, filename, FileUtils::fileLines, ignoreUnknown);
  }

  /**
   * @param filename      path of the file under e.g resources/filename
   * @param ignoreUnknown ignore extra values in when parsing
   * @return Try of class type T
   */
  public static Try<ConfigMap> parseYamlResource(String filename, boolean ignoreUnknown) {
    return parseYamlResource(ConfigMap.class, filename, ignoreUnknown);
  }

  /**
   * @param t             Class type
   * @param filename      path of the file under e.g resources/filename
   * @param ignoreUnknown ignore extra values in when parsing
   * @param <T>           Type param
   * @return Try of class type T
   */
  public static <T> Try<T> parseYamlResource(Class<T> t, String filename, boolean ignoreUnknown) {
    return parseYaml(t, filename, FileUtils::resourceLines, ignoreUnknown);
  }

  private static <T> Try<T> parseYaml(Class<T> t,
                                      String fileName,
                                      Function<String, Try<List<String>>> fileReader,
                                      boolean ignoreUnknown) {
    YamlConfig yc = new YamlConfig();
    yc.readConfig.setIgnoreUnknownProperties(ignoreUnknown);
    return fileReader.apply(fileName)
                     .map(list -> list.flatMap(line -> yamlInclude(line, fileReader))
                                      .reduceLeft((l1, l2) -> l1.appendAll(l2)))
                     .map(l -> String.join("\n", l))
                     .flatMap(s -> Try.of(() -> new YamlReader(s, yc).read(t)));
  }

  private static Try<List<String>> yamlInclude(String line, Function<String, Try<List<String>>> reader) {
    if (line.startsWith("include:")) {
      String file = line.split(":")[1].trim();
      return reader.apply(file);
    } else {
      return Try.success(List.of(line));
    }
  }
}

