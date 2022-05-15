package io.memoria.reactive.text.jackson.cases.company;

import io.memoria.reactive.core.eventsourcing.StateId;
import io.memoria.reactive.core.file.ResourceFileOps;
import io.memoria.reactive.core.id.Id;
import io.vavr.collection.List;

import java.time.LocalDate;

public class CompanyData {
  // Json Resources
  public static final String JSON_LIST;
  public static final String BOB_ENGINEER_JSON;
  public static final String ANNIKA_MANAGER_JSON;
  public static final String DEPARTMENT_JSON;
  public static final String BOB_PERSON_JSON;
  public static final String NAME_CREATED_JSON;
  // Yaml Resources
  public static final String BOB_ENGINEER_YAML;
  public static final String ANNIKA_MANAGER_YAML;
  public static final Engineer BOB_ENGINEER;
  public static final Engineer ALEX_ENGINEER;
  public static final Manager ANNIKA_MANAGER;
  public static final Person BOB_PERSON;

  static {
    // Json Resources
    JSON_LIST = ResourceFileOps.read("cases/company/json/List.json").get();
    BOB_ENGINEER_JSON = ResourceFileOps.read("cases/company/json/Engineer.json").get();
    ANNIKA_MANAGER_JSON = ResourceFileOps.read("cases/company/json/Manager.json").get();
    DEPARTMENT_JSON = ResourceFileOps.read("cases/company/json/Department.json").get();
    BOB_PERSON_JSON = ResourceFileOps.read("cases/company/json/Person.json").get();
    NAME_CREATED_JSON = ResourceFileOps.read("cases/company/json/NameCreated.json").get();

    BOB_ENGINEER_YAML = ResourceFileOps.read("cases/company/yaml/Engineer.yaml").get();
    ANNIKA_MANAGER_YAML = ResourceFileOps.read("cases/company/yaml/Manager.yaml").get();
    // Objects
    BOB_ENGINEER = new Engineer("bob", LocalDate.of(2000, 1, 1), List.of("fix issue 1", "Fix issue 2"));
    ALEX_ENGINEER = new Engineer("alex", LocalDate.of(2000, 1, 1), List.of("fix issue 3", "Fix issue 4"));
    ANNIKA_MANAGER = new Manager("Annika", List.of(BOB_ENGINEER, ALEX_ENGINEER));
    BOB_PERSON = new Person("bob", List.of("1", "2", "3").map(Id::of), List.of("1", "2", "3").map(StateId::of));
  }

  private CompanyData() {}
}
