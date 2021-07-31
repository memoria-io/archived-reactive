package io.memoria.reactive.text.jackson.cases.company;

import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.memoria.reactive.text.jackson.TestDeps.json;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonJacksonTest {

  @Test
  void deserializeDepartment() {
    // Given
    var expectedDepartment = new Department(List.of(CompanyData.ANNIKA_MANAGER,
                                                    CompanyData.BOB_ENGINEER,
                                                    CompanyData.ALEX_ENGINEER));
    // When
    var actualDepartment = json.deserialize(CompanyData.DEPARTMENT_JSON, Department.class).get();
    // Then
    assertEquals(expectedDepartment, actualDepartment);
  }

  @Test
  void deserializeEngineer() {
    // When
    var engineer = json.deserialize(CompanyData.BOB_ENGINEER_JSON, Engineer.class).get();
    // Then
    Assertions.assertEquals(CompanyData.BOB_ENGINEER.name(), engineer.name());
    Assertions.assertTrue(engineer.birthday().isEqual(CompanyData.BOB_ENGINEER.birthday()));
    Assertions.assertEquals(CompanyData.BOB_ENGINEER.tasks(), engineer.tasks());
  }

  @Test
  void deserializeManager() {
    // When
    var manager = json.deserialize(CompanyData.ANNIKA_MANAGER_JSON, Manager.class).get();
    // Then
    Assertions.assertEquals(CompanyData.ANNIKA_MANAGER.name(), manager.name());
    Assertions.assertEquals(CompanyData.BOB_ENGINEER, manager.team().get(0));
  }

  @Test
  void deserializeNameCreated() {
    // When
    var person = json.deserialize(CompanyData.NAME_CREATED_JSON, NameCreated.class).get();
    // Then
    Assertions.assertEquals(CompanyData.NAME_CREATED, person);
  }

  @Test
  void deserializePerson() {
    // When
    var person = json.deserialize(CompanyData.BOB_PERSON_JSON, Person.class).get();
    // Then
    Assertions.assertEquals(CompanyData.BOB_PERSON, person);
  }

  @Test
  void serializeNameCreated() {
    // When
    var bob = json.serialize(CompanyData.NAME_CREATED).get();
    // Then
    assertEquals(CompanyData.NAME_CREATED_JSON, bob);
  }

  @Test
  void serializePerson() {
    // When
    var bob = json.serialize(CompanyData.BOB_PERSON).get();
    // Then
    assertEquals(CompanyData.BOB_PERSON_JSON, bob);
  }
}
