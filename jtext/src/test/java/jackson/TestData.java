package jackson;

import io.memoria.jutils.core.id.Id;
import io.memoria.jutils.jtext.transformer.json.Json;
import io.memoria.jutils.jtext.transformer.yaml.Yaml;
import io.memoria.jutils.core.utils.file.FileUtils;
import jackson.transformer.Employee;
import jackson.transformer.Engineer;
import io.memoria.jutils.jackson.transformer.JacksonUtils;
import jackson.transformer.Manager;
import jackson.transformer.NameCreated;
import jackson.transformer.Person;
import io.memoria.jutils.jackson.transformer.json.JsonJackson;
import io.memoria.jutils.jackson.transformer.yaml.YamlJackson;
import io.vavr.collection.List;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestData {
  public static final FileUtils files;
  public static final Json json;
  public static final Yaml yaml;

  // Json Resources
  public static final String JSON_LIST;
  public static final String BOB_ENGINEER_JSON;
  public static final String ANNIKA_MANAGER_JSON;
  public static final String DEPARTMENT_JSON;
  public static final String BOB_PERSON_JSON;
  public static final String NAME_CREATED_JSON;
  // Yaml Resources
  public static final String APP_CONFIG_YAML;
  public static final String BOB_ENGINEER_YAML;
  public static final String ANNIKA_MANAGER_YAML;
  public static final Engineer BOB_ENGINEER;
  public static final Engineer ALEX_ENGINEER;
  public static final Manager ANNIKA_MANAGER;
  public static final Person BOB_PERSON;
  public static final NameCreated NAME_CREATED;

  static {
    // File utils
    files = FileUtils.build(Schedulers.boundedElastic());
    // Json
    var jsonMapper = JacksonUtils.mixinPropertyFormat(JacksonUtils.defaultJson(), Employee.class);
    jsonMapper.registerSubtypes(Manager.class, Engineer.class);
    json = new JsonJackson(jsonMapper);
    // Yaml
    yaml = new YamlJackson(JacksonUtils.defaultYaml());
    // Json Resources
    JSON_LIST = files.readResource("io/memoria/jutils/jtext/transformer/json/List.json").block();
    BOB_ENGINEER_JSON = files.readResource("io/memoria/jutils/jtext/transformer/json/Engineer.json").block();
    ANNIKA_MANAGER_JSON = files.readResource("io/memoria/jutils/jtext/transformer/json/Manager.json").block();
    DEPARTMENT_JSON = files.readResource("io/memoria/jutils/jtext/transformer/json/Department.json").block();
    BOB_PERSON_JSON = files.readResource("io/memoria/jutils/jtext/transformer/json/Person.json").block();
    NAME_CREATED_JSON = files.readResource("io/memoria/jutils/jtext/transformer/json/NameCreated.json").block();
    // Yaml Resources
    APP_CONFIG_YAML = files.readResource("io/memoria/jutils/jtext/transformer/yaml/AppConfigs.yaml", "include:").block();
    BOB_ENGINEER_YAML = files.readResource("io/memoria/jutils/jtext/transformer/yaml/Engineer.yaml").block();
    ANNIKA_MANAGER_YAML = files.readResource("io/memoria/jutils/jtext/transformer/yaml/Manager.yaml").block();
    // Objects
    BOB_ENGINEER = new Engineer("bob", LocalDate.of(2000, 1, 1), List.of("fix issue 1", "Fix issue 2"));
    ALEX_ENGINEER = new Engineer("alex", LocalDate.of(2000, 1, 1), List.of("fix issue 3", "Fix issue 4"));
    ANNIKA_MANAGER = new Manager("Annika", List.of(BOB_ENGINEER, ALEX_ENGINEER));
    BOB_PERSON = new Person("bob", List.of("1", "2", "3").map(Id::of));
    NAME_CREATED = new NameCreated(Id.of(1), Id.of(2), "bob", LocalDateTime.of(2020, 12, 1, 11, 0));
  }

  private TestData() {}
}
