package io.memoria.jutils.jackson.transformer.json;

import io.memoria.jutils.jackson.Tests;
import io.memoria.jutils.jackson.transformer.Department;
import io.memoria.jutils.jackson.transformer.Engineer;
import io.memoria.jutils.jackson.transformer.Manager;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JsonJacksonTest {

  @Test
  void toDepartment() {
    // Given
    var expectedDepartment = new Department(List.of(Tests.ANNIKA_MANAGER, Tests.BOB_ENGINEER, Tests.ALEX_ENGINEER));
    // When
    var actualDepartment = Tests.json.deserialize(Tests.DEPARTMENT_JSON, Department.class).get();
    // Then
    Assertions.assertEquals(expectedDepartment, actualDepartment);
  }

  @Test
  void toEngineer() {
    // When
    var engineer = Tests.json.deserialize(Tests.BOB_ENGINEER_JSON, Engineer.class).get();
    // Then
    Assertions.assertEquals(Tests.BOB_ENGINEER.name(), engineer.name());
    Assertions.assertTrue(engineer.birthday().isEqual(Tests.BOB_ENGINEER.birthday()));
    Assertions.assertEquals(Tests.BOB_ENGINEER.tasks(), engineer.tasks());
  }

  @Test
  void toManager() {
    // When
    var manager = Tests.json.deserialize(Tests.ANNIKA_MANAGER_JSON, Manager.class).get();
    // Then
    Assertions.assertEquals(Tests.ANNIKA_MANAGER.name(), manager.name());
    Assertions.assertEquals(Tests.BOB_ENGINEER, manager.team().get(0));
  }
}
