package io.memoria.jutils.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.memoria.jutils.core.transformer.json.Json;
import io.memoria.jutils.core.transformer.yaml.Yaml;
import io.memoria.jutils.core.utils.file.FileUtils;
import io.memoria.jutils.jackson.transformer.json.JsonJackson;
import io.memoria.jutils.jackson.transformer.yaml.YamlJackson;
import io.vavr.jackson.datatype.VavrModule;
import reactor.core.scheduler.Schedulers;

public class Tests {
  public static final FileUtils files;
  public static final Json json;
  public static final Yaml yaml;

  // Json Resources
  public static final String JSON_LIST;
  public static final String JSON_ENGINEER;
  public static final String JSON_MANAGER;
  // Yaml Resources
  public static final String YAML_APP_CONFIG;
  public static final String YAML_ENGINEER;
  public static final String YAML_MANAGER;

  static {
    // File utils
    files = new FileUtils(Schedulers.elastic());
    // Json
    ObjectMapper jsonMapper = new ObjectMapper(new JsonFactory());
    jsonMapper.registerModule(new VavrModule());
    json = new JsonJackson(jsonMapper);
    // Yaml
    ObjectMapper jacksonMapper = new ObjectMapper(new YAMLFactory());
    jacksonMapper.registerModule(new VavrModule());
    yaml = new YamlJackson(jacksonMapper);
    // Json Resources
    JSON_LIST = files.readResource("transformer/json/List.json").block();
    JSON_ENGINEER = files.readResource("transformer/json/Engineer.json").block();
    JSON_MANAGER = files.readResource("transformer/json/Manager.json").block();
    // Yaml Resources
    YAML_APP_CONFIG = files.readResource("transformer/yaml/AppConfigs.yaml", "include:").block();
    YAML_ENGINEER = files.readResource("transformer/yaml/Engineer.yaml").block();
    YAML_MANAGER = files.readResource("transformer/yaml/Manager.yaml").block();
  }

  private Tests() {}
}
