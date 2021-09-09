package io.memoria.reactive.text.jackson.cases.company;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static io.memoria.reactive.text.jackson.TestDeps.yaml;

class YamlJacksonTest {

  @Test
  void serializeEngineer() {
    var yamlEngineerMono = yaml.serialize(CompanyData.BOB_ENGINEER);
    assert CompanyData.BOB_ENGINEER_YAML != null;
    StepVerifier.create(yamlEngineerMono).expectNext(CompanyData.BOB_ENGINEER_YAML).verifyComplete();
  }

  @Test
  void serializeManager() {
    var yamlEngineerMono = yaml.serialize(CompanyData.ANNIKA_MANAGER);
    assert CompanyData.ANNIKA_MANAGER_YAML != null;
    StepVerifier.create(yamlEngineerMono).expectNext(CompanyData.ANNIKA_MANAGER_YAML).verifyComplete();
  }

  @Test
  void toEngineer() {
    // When
    var engineerMono = yaml.deserialize(CompanyData.BOB_ENGINEER_YAML, Engineer.class);
    // Then
    StepVerifier.create(engineerMono).expectNext(CompanyData.BOB_ENGINEER).verifyComplete();
  }

  @Test
  void toManager() {
    // When
    var managerMono = yaml.deserialize(CompanyData.ANNIKA_MANAGER_YAML, Manager.class);
    // Then
    StepVerifier.create(managerMono).expectNext(CompanyData.ANNIKA_MANAGER).verifyComplete();
  }
}
