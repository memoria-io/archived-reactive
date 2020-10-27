package io.memoria.jutils.jackson.transformer.yaml;

import io.memoria.jutils.jackson.Tests;
import io.memoria.jutils.jackson.transformer.Engineer;
import io.memoria.jutils.jackson.transformer.Manager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class YamlJacksonTest {

  @Test
  void serializeEngineer() {
    var yamlEngineer = Tests.yaml.serialize(Tests.BOB_ENGINEER).get();
    Assertions.assertEquals(Tests.BOB_ENGINEER_YAML, yamlEngineer);
  }

  @Test
  void toEngineer() {
    // When
    var engineer = Tests.yaml.deserialize(Tests.BOB_ENGINEER_YAML, Engineer.class).get();
    // Then
    Assertions.assertEquals(Tests.BOB_ENGINEER.name(), engineer.name());
    Assertions.assertEquals(Tests.BOB_ENGINEER.tasks(), engineer.tasks());
  }

  @Test
  void toManager() {
    // When
    var manager = Tests.yaml.deserialize(Tests.ANNIKA_MANAGER_YAML, Manager.class).get();
    // Then
    Assertions.assertEquals(Tests.ANNIKA_MANAGER.name(), manager.name());
    Assertions.assertEquals(Tests.BOB_ENGINEER, manager.team().get(0));
  }
}
