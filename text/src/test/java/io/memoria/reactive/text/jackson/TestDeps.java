package io.memoria.reactive.text.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.reactive.core.file.Configs;
import io.memoria.reactive.core.text.Json;
import io.memoria.reactive.core.text.Yaml;
import io.memoria.reactive.text.jackson.cases.company.Employee;
import io.memoria.reactive.text.jackson.cases.company.Engineer;
import io.memoria.reactive.text.jackson.cases.company.Manager;

public class TestDeps {
  public static final Configs configs;
  public static final Json prettyJson;
  public static final Json compactJson;
  public static final Yaml yaml;

  static {
    // File utils
    configs = new Configs("include:", false);
    // Json
    prettyJson = new JsonJackson(jacksonJsonMapper(true));
    compactJson = new JsonJackson(jacksonJsonMapper(false));
    // Yaml
    yaml = new YamlJackson(JacksonUtils.yaml());
  }

  private static ObjectMapper jacksonJsonMapper(boolean isPretty) {
    var jsonOM = (isPretty) ? JacksonUtils.prettyJson() : JacksonUtils.json();
    var jsonMapper = JacksonUtils.mixinPropertyFormat(jsonOM, Employee.class);
    jsonMapper.registerSubtypes(Manager.class, Engineer.class);
    return jsonMapper;
  }
}
