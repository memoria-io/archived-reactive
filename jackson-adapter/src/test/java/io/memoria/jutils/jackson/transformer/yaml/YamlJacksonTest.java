package io.memoria.jutils.jackson.transformer.yaml;

import io.memoria.jutils.jackson.TestData;
import io.memoria.jutils.jackson.transformer.Engineer;
import io.memoria.jutils.jackson.transformer.Manager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class YamlJacksonTest {

  @Test
  void serializeEngineer() {
    var yamlEngineer = TestData.yaml.serialize(TestData.BOB_ENGINEER).get();
    Assertions.assertEquals(TestData.BOB_ENGINEER_YAML, yamlEngineer);
  }

  @Test
  void toEngineer() {
    // When
    var engineer = TestData.yaml.deserialize(TestData.BOB_ENGINEER_YAML, Engineer.class).get();
    // Then
    Assertions.assertEquals(TestData.BOB_ENGINEER.name(), engineer.name());
    Assertions.assertEquals(TestData.BOB_ENGINEER.tasks(), engineer.tasks());
  }

  @Test
  void toManager() {
    // When
    var manager = TestData.yaml.deserialize(TestData.ANNIKA_MANAGER_YAML, Manager.class).get();
    // Then
    Assertions.assertEquals(TestData.ANNIKA_MANAGER.name(), manager.name());
    Assertions.assertEquals(TestData.BOB_ENGINEER, manager.team().get(0));
  }
}
