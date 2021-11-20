package io.memoria.reactive.text.jackson.cases.company;

import io.memoria.reactive.core.file.Resources;
import io.memoria.reactive.core.id.Id;
import io.vavr.collection.List;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
  public static final NameCreated NAME_CREATED;

  static {
    // Json Resources
    JSON_LIST = Resources.read("cases/company/json/List.json").get();
    BOB_ENGINEER_JSON = Resources.read("cases/company/json/Engineer.json").get();
    ANNIKA_MANAGER_JSON = Resources.read("cases/company/json/Manager.json").get();
    DEPARTMENT_JSON = Resources.read("cases/company/json/Department.json").get();
    BOB_PERSON_JSON = Resources.read("cases/company/json/Person.json").get();
    NAME_CREATED_JSON = Resources.read("cases/company/json/NameCreated.json").get();

    BOB_ENGINEER_YAML = Resources.read("cases/company/yaml/Engineer.yaml").get();
    ANNIKA_MANAGER_YAML = Resources.read("cases/company/yaml/Manager.yaml").get();
    // Objects
    BOB_ENGINEER = new Engineer("bob", LocalDate.of(2000, 1, 1), List.of("fix issue 1", "Fix issue 2"));
    ALEX_ENGINEER = new Engineer("alex", LocalDate.of(2000, 1, 1), List.of("fix issue 3", "Fix issue 4"));
    ANNIKA_MANAGER = new Manager("Annika", List.of(BOB_ENGINEER, ALEX_ENGINEER));
    BOB_PERSON = new Person("bob", List.of("1", "2", "3").map(Id::of));
    NAME_CREATED = new NameCreated(Id.of(2), "bob", LocalDateTime.of(2020, 12, 1, 11, 0));
  }

  private CompanyData() {}
}
