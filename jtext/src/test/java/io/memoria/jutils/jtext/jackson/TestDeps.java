package io.memoria.jutils.jtext.jackson;

import io.memoria.jutils.jcore.file.FileUtils;
import io.memoria.jutils.jcore.text.Json;
import io.memoria.jutils.jcore.text.Yaml;
import io.memoria.jutils.jtext.jackson.cases.company.Employee;
import io.memoria.jutils.jtext.jackson.cases.company.Engineer;
import io.memoria.jutils.jtext.jackson.cases.company.Manager;
import reactor.core.scheduler.Schedulers;

import static io.vavr.control.Option.some;

public class TestDeps {
  public static final FileUtils fileUtils;
  public static final Json json;
  public static final Yaml yaml;

  static {
    // File utils
    fileUtils = FileUtils.createDefault(some("include:"), false, Schedulers.boundedElastic());
    // Json
    var jsonMapper = JacksonUtils.mixinPropertyFormat(JacksonUtils.defaultJson(), Employee.class);
    jsonMapper.registerSubtypes(Manager.class, Engineer.class);
    json = new JsonJackson(jsonMapper);
    // Yaml
    yaml = new YamlJackson(JacksonUtils.defaultYaml());
  }
}
