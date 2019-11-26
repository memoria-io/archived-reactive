package com.marmoush.jutils.yaml;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import io.vavr.collection.List;
import io.vavr.control.Try;

import java.util.HashMap;

import static com.marmoush.jutils.file.FileUtils.resourceLines;

public class YamlUtils {
  private YamlUtils() {}

  public static class ConfigMap extends HashMap<String, Object> {}

  /**
   * Supports one level expansion of another file through "include: file.txt"
   *
   * @param fileName resource filename
   * @return object instance of class ConfigMap
   */
  public static Try<ConfigMap> parseYamlResource(String fileName) {
    return parseYamlResource(ConfigMap.class, fileName);
  }

  /**
   * Supports one level expansion of another file through "include: file.txt"
   *
   * @param t        Class type T
   * @param fileName resource filename
   * @param <T>      Parse file to class T
   * @return object instance of class T
   */
  public static <T> Try<T> parseYamlResource(Class<T> t, String fileName) {
    return parseYamlResource(t, fileName, true);
  }

  /**
   * Supports one level expansion of another file through "include: file.txt"
   *
   * @param t             Class type T
   * @param fileName      resource filename
   * @param <T>           Parse file to class T
   * @param ignoreUnknown ignore unknown extra values
   * @return object instance of class T
   */
  public static <T> Try<T> parseYamlResource(Class<T> t, String fileName, boolean ignoreUnknown) {
    YamlConfig yc = new YamlConfig();
    yc.readConfig.setIgnoreUnknownProperties(ignoreUnknown);
    Try<String> str = resourceLines(fileName).map(tr -> tr.flatMap(YamlUtils::yamlInclude)
                                                          .reduceLeft((l1, l2) -> l1.appendAll(l2)))
                                             .map(l -> String.join("\n", l));
    return str.flatMap(s -> Try.of(() -> new YamlReader(s, yc).read(t)));
  }

  private static Try<List<String>> yamlInclude(String l) {
    if (l.startsWith("include:")) {
      String file = l.split(":")[1].trim();
      return resourceLines(file);
    } else {
      return Try.success(List.of(l));
    }
  }
}

