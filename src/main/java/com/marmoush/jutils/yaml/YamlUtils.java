package com.marmoush.jutils.yaml;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import io.vavr.control.Try;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.Collectors;

import static com.marmoush.jutils.file.FileUtils.asStringBlocking;
import static com.marmoush.jutils.file.FileUtils.resource;

public class YamlUtils {
  private YamlUtils() {}

  public static class ConfigMap extends HashMap<String, Object> {}

  public static Try<ConfigMap> parseYaml(String fileName) {
    return parseYaml(ConfigMap.class, fileName);
  }

  public static <T> Try<T> parseYaml(Class<T> t, String fileName) {
    return parseYaml(t, fileName, true);
  }

  public static <T> Try<T> parseYaml(Class<T> t, String fileName, boolean ignoreUnknown) {
    YamlConfig yc = new YamlConfig();
    yc.readConfig.setIgnoreUnknownProperties(ignoreUnknown);
    YamlReader yr = new YamlReader(asStringBlocking(resource(fileName), YamlUtils::yamlInclude), yc);
    return Try.of(() -> yr.read(t));
  }

  private static String yamlInclude(String l) {
    if (l.startsWith("include:")) {
      String file = l.split(":")[1].trim();
      InputStream is = resource(file);
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    } else {
      return l;
    }
  }
}

