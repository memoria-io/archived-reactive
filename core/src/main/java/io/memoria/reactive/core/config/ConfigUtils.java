package io.memoria.reactive.core.config;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

public class ConfigUtils {
  public static Map<String, String> readMainArgs(String[] args) {
    var entries = List.of(args).map(arg -> arg.split("=")).map(arg -> Tuple.of(arg[0], arg[1]));
    return HashMap.ofEntries(entries);
  }

  private ConfigUtils() {}
}
