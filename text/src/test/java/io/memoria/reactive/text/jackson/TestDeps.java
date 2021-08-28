package io.memoria.reactive.text.jackson;

import io.memoria.reactive.core.file.RConfig;
import io.memoria.reactive.core.text.Json;
import io.memoria.reactive.core.text.Yaml;
import io.memoria.reactive.text.jackson.cases.company.Employee;
import io.memoria.reactive.text.jackson.cases.company.Engineer;
import io.memoria.reactive.text.jackson.cases.company.Manager;

public class TestDeps {
  public static final RConfig CONFIG_UTILS;
  public static final Json json;
  public static final Yaml yaml;

  static {
    // File utils
    CONFIG_UTILS = new RConfig("include:", false);
    // Json
    var jsonMapper = JacksonUtils.mixinPropertyFormat(JacksonUtils.defaultJson(), Employee.class);
    jsonMapper.registerSubtypes(Manager.class, Engineer.class);
    json = new JsonJackson(jsonMapper);
    // Yaml
    yaml = new YamlJackson(JacksonUtils.defaultYaml());
  }
}
