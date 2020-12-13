package io.memoria.jutils.jconf;

import io.memoria.jutils.core.transformer.StringTransformer;
import io.memoria.jutils.core.utils.file.FileUtils;
import io.memoria.jutils.jackson.transformer.JacksonUtils;
import io.memoria.jutils.jackson.transformer.yaml.YamlJackson;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static io.memoria.jutils.core.utils.functional.ReactorVavrUtils.toMono;

public class JConf {
  private final FileUtils fileUtils;
  private final StringTransformer yamlReader;

  public static String property(String match) {
    var exp = match.replace("${", "").replace("}", "").trim();
    var split = exp.split(":-");
    if (split.length == 2) {
      return Option.of(System.getProperty(split[0]))
                   .orElse(() -> Option.of(System.getenv(split[0])))
                   .getOrElse(split[1]);
    }
    if (split.length == 1) {
      return Option.of(System.getProperty(split[0])).orElse(() -> Option.of(System.getenv(split[0]))).getOrElse(match);
    }
    return match;
  }

  public static String resolve(String line) {
    var p = Pattern.compile("\\$\\{[\\sa-zA-Z_0-9]+(:-)?[\\sa-zA-Z_0-9]+}");//NOSONAR
    var f = p.matcher(line);
    Map<String, String> matches = new HashMap<>();
    while (f.find()) {
      var match = line.substring(f.start(), f.end());
      matches.put(match, property(match));
    }
    for (Entry<String, String> entry : matches.entrySet()) {
      line = line.replace(entry.getKey(), entry.getValue());
    }
    return line;
  }

  public JConf(Scheduler scheduler) {
    this.fileUtils = new FileUtils(scheduler);
    var yom = JacksonUtils.defaultYaml();
    JacksonUtils.mixinPropertyFormat(yom);
    this.yamlReader = new YamlJackson(yom);
  }

  public <T> Mono<T> read(Path path, Class<T> as) {
    return resolve(fileUtils.readLines(path, "include:"), as);
  }

  public <T> Mono<T> readResource(String path, Class<T> as) {
    return resolve(fileUtils.readResourceLines(path, "include:"), as);

  }

  private <T> Mono<T> resolve(Flux<String> lines, Class<T> as) {
    return lines.map(JConf::resolve)
                .reduce((a, b) -> a + System.lineSeparator() + b)
                .flatMap(s -> toMono(yamlReader.deserialize(s, as)));
  }
}
