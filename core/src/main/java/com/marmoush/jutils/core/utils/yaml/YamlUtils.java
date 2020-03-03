package com.marmoush.jutils.core.utils.yaml;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.marmoush.jutils.core.utils.file.FileUtils;
import io.vavr.collection.List;
import io.vavr.control.Try;

import java.util.HashMap;
import java.util.function.Function;

public class YamlUtils {
  private YamlUtils() {}

  private static final class MapInstance extends HashMap<String, Object> {}

  /**
   * @param filename      path of the file
   * @param ignoreUnknown ignore extra values in when parsing
   * @return Try of class type T
   */
  public static Try<YamlConfigMap> parseYamlFile(String filename, boolean ignoreUnknown) {
    return parseYamlFile(MapInstance.class, filename, ignoreUnknown).map(YamlConfigMap::new);
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
   * @param filename path of the file under e.g resources/filename
   * @return Try of class type T
   */
  public static Try<YamlConfigMap> parseYamlResource(String filename) {
    return parseYamlResource(MapInstance.class, filename, false).map(YamlConfigMap::new);
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
                     .map(list -> list.flatMap(line -> yamlInclude(line, fileReader)).reduceLeft(List::appendAll))
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

