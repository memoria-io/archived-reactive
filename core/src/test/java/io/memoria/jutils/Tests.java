package io.memoria.jutils;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.core.utils.yaml.YamlUtils;
import io.vavr.Function1;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Tests {
  public static final Function1<String, Mono<YamlConfigMap>> YAML_RESOURCE_PARSER = YamlUtils.parseYamlResource(
          Schedulers.elastic());
  public static final Function1<String, Mono<YamlConfigMap>> YAML_FILE_PARSER = YamlUtils.parseYamlFile(Schedulers.elastic());

  private Tests() {}
}
