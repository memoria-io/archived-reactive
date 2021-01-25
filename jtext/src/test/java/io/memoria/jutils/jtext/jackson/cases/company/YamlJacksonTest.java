package io.memoria.jutils.jtext.jackson.cases.company;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.jutils.jtext.jackson.TestDeps.yaml;

class YamlJacksonTest {

  @Test
  void serializeEngineer() {
    var yamlEngineer = yaml.serialize(CompanyData.BOB_ENGINEER).get();
    Assertions.assertEquals(CompanyData.BOB_ENGINEER_YAML, yamlEngineer);
  }

  @Test
  void serializeManager() {
    var yamlEngineer = yaml.serialize(CompanyData.ANNIKA_MANAGER).get();
    Assertions.assertEquals(CompanyData.ANNIKA_MANAGER_YAML, yamlEngineer);
  }

  @Test
  void toEngineer() {
    // When
    var engineer = yaml.deserialize(CompanyData.BOB_ENGINEER_YAML, Engineer.class).get();
    // Then
    Assertions.assertEquals(CompanyData.BOB_ENGINEER.name(), engineer.name());
    Assertions.assertEquals(CompanyData.BOB_ENGINEER.tasks(), engineer.tasks());
  }

  @Test
  void toManager() {
    // When
    var manager = yaml.deserialize(CompanyData.ANNIKA_MANAGER_YAML, Manager.class).get();
    // Then
    Assertions.assertEquals(CompanyData.ANNIKA_MANAGER.name(), manager.name());
    Assertions.assertEquals(CompanyData.BOB_ENGINEER, manager.team().get(0));
  }
}
