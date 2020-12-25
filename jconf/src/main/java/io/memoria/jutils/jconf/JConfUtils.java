package io.memoria.jutils.jconf;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map.Entry;
import java.util.regex.Pattern;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public class JConfUtils {
  public static Map<String, String> readMainArgs(String[] args) {
    var entries = List.of(args).map(arg -> arg.split("=")).map(arg -> Tuple.of(arg[0], arg[1]));
    return HashMap.ofEntries(entries);
  }

  public static Map<String, String> readSysEnv() {
    return HashMap.ofAll(System.getenv());
  }

  public static Option<String> resolveExpression(String expression, Map<String, String> properties) {
    var exp = expression.replace("${", "").replace("}", "").trim();
    var split = exp.split(":-");
    if (split.length == 2) {
      var key = split[0];
      var defaultValue = split[1];
      var value = properties.get(key).getOrElse(defaultValue);
      return some(value);
    }
    if (split.length == 1) {
      var key = split[0];
      return properties.get(key);
    }
    return none();
  }

  public static String resolveLine(String line, Map<String, String> properties) {
    var p = Pattern.compile("\\$\\{[\\sa-zA-Z_0-9]+(:-)?[\\sa-zA-Z_0-9]+}");//NOSONAR
    var f = p.matcher(line);
    var matches = new java.util.HashMap<String, String>();
    while (f.find()) {
      var match = line.substring(f.start(), f.end());
      matches.put(match, resolveExpression(match, properties).getOrElse(match));
    }
    for (Entry<String, String> entry : matches.entrySet()) {
      line = line.replace(entry.getKey(), entry.getValue());
    }
    return line;
  }

  public static Mono<String> resolveLines(Flux<String> lines, Map<String, String> properties) {
    return lines.map(l -> resolveLine(l, properties)).reduce((a, b) -> a + System.lineSeparator() + b);
  }
}
