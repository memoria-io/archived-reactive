package io.memoria.reactive.core.file;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map.Entry;
import java.util.regex.Pattern;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public class ConfigUtils {

  private final Option<String> nestingPrefix;
  private final boolean resolveSystemEnv;
  private final Map<String, String> systemEnv;

  /**
   * @param resolveSystemEnv when true, any line which contains ${ENV_VALUE:-defaultValue} will be resolved from system
   *                         environment
   */
  public ConfigUtils(String nestingPrefix, boolean resolveSystemEnv) {
    this.resolveSystemEnv = resolveSystemEnv;
    this.systemEnv = (resolveSystemEnv) ? HashMap.ofAll(System.getenv()) : HashMap.empty();
    this.nestingPrefix = Option.of(nestingPrefix).flatMap(s -> (s.isEmpty()) ? none() : some(s));
  }

  public ConfigUtils(boolean resolveSystemEnv) {
    this(null, resolveSystemEnv);
  }

  public Mono<String> read(String path) {
    return readLines(path).reduce(RFile.JOIN_LINES);
  }

  public Flux<String> readLines(String path) {
    return RFile.readLines(path).concatMap(l -> expand(path, l)).map(this::resolveLine);
  }

  private Flux<String> expand(String path, String line) {
    if (nestingPrefix.isDefined() && line.trim().startsWith(nestingPrefix.get())) {
      var subFilePath = line.substring(nestingPrefix.get().length()).trim();
      var relativePath = RFile.parentPath(path) + subFilePath;
      return readLines(relativePath);
    } else {
      return Flux.just(line);
    }
  }

  private Option<String> resolveExpression(String expression) {
    var exp = expression.replace("${", "").replace("}", "").trim();
    var split = exp.split(":-");
    if (split.length == 1) {
      var key = split[0];
      return this.systemEnv.get(key).orElse(none());
    }
    if (split.length == 2) {
      var key = split[0];
      var defaultValue = split[1];
      return this.systemEnv.get(key).orElse(some(defaultValue));
    }
    return none();
  }

  private String resolveLine(String line) {
    if (this.resolveSystemEnv) {
      var p = Pattern.compile("\\$\\{[\\sa-zA-Z_0-9]+(:-)?.+}");//NOSONAR
      var f = p.matcher(line);
      var matches = new java.util.HashMap<String, String>();
      while (f.find()) {
        var match = line.substring(f.start(), f.end());
        matches.put(match, resolveExpression(match).getOrElse(match));
      }
      for (Entry<String, String> entry : matches.entrySet()) {
        line = line.replace(entry.getKey(), entry.getValue());
      }
    }
    return line;
  }
}
