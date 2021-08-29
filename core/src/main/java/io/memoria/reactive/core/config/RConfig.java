package io.memoria.reactive.core.config;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.regex.Pattern;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public class RConfig {
  private static final BinaryOperator<String> JOIN_LINES = (a, b) -> a + System.lineSeparator() + b;
  private final Option<String> nestingPrefix;
  private final boolean resolveSystemEnv;
  private final Map<String, String> systemEnv;

  /**
   * @param resolveSystemEnv when true, any line which contains ${ENV_VALUE:-defaultValue} will be resolved from system
   *                         environment
   */
  public RConfig(String nestingPrefix, boolean resolveSystemEnv) {
    this.resolveSystemEnv = resolveSystemEnv;
    this.systemEnv = (resolveSystemEnv) ? HashMap.ofAll(System.getenv()) : HashMap.empty();
    this.nestingPrefix = Option.of(nestingPrefix).flatMap(s -> (s.isEmpty()) ? none() : some(s));
  }

  public RConfig(boolean resolveSystemEnv) {
    this(null, resolveSystemEnv);
  }

  /**
   * if the path parameter doesn't start with "/" it's considered a file under the resources directory
   */
  public Mono<String> read(String path) {
    return readLines(path).reduce(JOIN_LINES);
  }

  private Flux<String> expand(String path, String line) {
    if (nestingPrefix.isDefined() && line.trim().startsWith(nestingPrefix.get())) {
      var subFilePath = line.substring(nestingPrefix.get().length()).trim();
      var relativePath = parentPath(path) + subFilePath;
      return readLines(relativePath);
    } else {
      return Flux.just(line);
    }
  }

  private Flux<String> readLines(String path) {
    return readResourceOrFile(path).concatMap(l -> expand(path, l)).map(this::resolveLine);
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

  private static String parentPath(String filePath) {
    return filePath.replaceFirst("[^/]+$", ""); //NOSONAR
  }

  private static List<String> readResource(String path) throws IOException {
    try (var inputStream = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(path))) {
      var result = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) != -1) {
        result.write(buffer, 0, length);
      }
      var file = result.toString(StandardCharsets.UTF_8);
      return List.of(file.split("\\r?\\n"));
    }
  }

  private static Flux<String> readResourceOrFile(String path) {
    if (path.startsWith("/")) {
      return Mono.fromCallable(() -> Files.lines(Path.of(path))).flatMapMany(Flux::fromStream);
    } else {
      return Mono.fromCallable(() -> readResource(path)).flatMapMany(Flux::fromIterable);
    }
  }
}
