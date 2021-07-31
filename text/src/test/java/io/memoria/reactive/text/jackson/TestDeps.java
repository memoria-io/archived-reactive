package io.memoria.reactive.text.jackson;

import io.memoria.reactive.core.file.FileUtils;
import io.memoria.reactive.core.text.Json;
import io.memoria.reactive.core.text.Yaml;
import io.memoria.reactive.text.jackson.cases.company.Employee;
import io.memoria.reactive.text.jackson.cases.company.Engineer;
import io.memoria.reactive.text.jackson.cases.company.Manager;
import reactor.core.scheduler.Schedulers;

public class TestDeps {
  public static final FileUtils fileUtils;
  public static final Json json;
  public static final Yaml yaml;

  static {
    // File utils
    fileUtils = FileUtils.createDefault("include:", false, Schedulers.boundedElastic());
    // Json
    var jsonMapper = JacksonUtils.mixinPropertyFormat(JacksonUtils.defaultJson(), Employee.class);
    jsonMapper.registerSubtypes(Manager.class, Engineer.class);
    json = new JsonJackson(jsonMapper);
    // Yaml
    yaml = new YamlJackson(JacksonUtils.defaultYaml());
  }
}
