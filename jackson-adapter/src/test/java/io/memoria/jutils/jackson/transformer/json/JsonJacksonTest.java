package io.memoria.jutils.jackson.transformer.json;

import io.memoria.jutils.jackson.Tests;
import io.memoria.jutils.jackson.transformer.Department;
import io.memoria.jutils.jackson.transformer.Engineer;
import io.memoria.jutils.jackson.transformer.Manager;
import io.memoria.jutils.jackson.transformer.NameCreated;
import io.memoria.jutils.jackson.transformer.Person;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonJacksonTest {

  @Test
  void deserializeDepartment() {
    // Given
    var expectedDepartment = new Department(List.of(Tests.ANNIKA_MANAGER, Tests.BOB_ENGINEER, Tests.ALEX_ENGINEER));
    // When
    var actualDepartment = Tests.json.deserialize(Tests.DEPARTMENT_JSON, Department.class).get();
    // Then
    assertEquals(expectedDepartment, actualDepartment);
  }

  @Test
  void deserializeEngineer() {
    // When
    var engineer = Tests.json.deserialize(Tests.BOB_ENGINEER_JSON, Engineer.class).get();
    // Then
    assertEquals(Tests.BOB_ENGINEER.name(), engineer.name());
    Assertions.assertTrue(engineer.birthday().isEqual(Tests.BOB_ENGINEER.birthday()));
    assertEquals(Tests.BOB_ENGINEER.tasks(), engineer.tasks());
  }

  @Test
  void deserializeManager() {
    // When
    var manager = Tests.json.deserialize(Tests.ANNIKA_MANAGER_JSON, Manager.class).get();
    // Then
    assertEquals(Tests.ANNIKA_MANAGER.name(), manager.name());
    assertEquals(Tests.BOB_ENGINEER, manager.team().get(0));
  }

  @Test
  void deserializeNameCreated() {
    // When
    var person = Tests.json.deserialize(Tests.NAME_CREATED_JSON, NameCreated.class).get();
    // Then
    assertEquals(Tests.NAME_CREATED, person);
  }

  @Test
  void deserializePerson() {
    // When
    var person = Tests.json.deserialize(Tests.BOB_PERSON_JSON, Person.class).get();
    // Then
    assertEquals(Tests.BOB_PERSON, person);
  }

  @Test
  void serializeNameCreated() {
    // When
    var bob = Tests.json.serialize(Tests.NAME_CREATED).get();
    // Then
    assertEquals(Tests.NAME_CREATED_JSON, bob);
  }

  @Test
  void serializePerson() {
    // When
    var bob = Tests.json.serialize(Tests.BOB_PERSON).get();
    // Then
    assertEquals(Tests.BOB_PERSON_JSON, bob);
  }
}
