package jackson.transformer.json;

import jackson.TestData;
import jackson.transformer.Department;
import jackson.transformer.Engineer;
import jackson.transformer.Manager;
import jackson.transformer.NameCreated;
import jackson.transformer.Person;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonJacksonTest {

  @Test
  void deserializeDepartment() {
    // Given
    var expectedDepartment = new Department(List.of(TestData.ANNIKA_MANAGER,
                                                    TestData.BOB_ENGINEER,
                                                    TestData.ALEX_ENGINEER));
    // When
    var actualDepartment = TestData.json.deserialize(TestData.DEPARTMENT_JSON, Department.class).get();
    // Then
    assertEquals(expectedDepartment, actualDepartment);
  }

  @Test
  void deserializeEngineer() {
    // When
    var engineer = TestData.json.deserialize(TestData.BOB_ENGINEER_JSON, Engineer.class).get();
    // Then
    Assertions.assertEquals(TestData.BOB_ENGINEER.name(), engineer.name());
    Assertions.assertTrue(engineer.birthday().isEqual(TestData.BOB_ENGINEER.birthday()));
    Assertions.assertEquals(TestData.BOB_ENGINEER.tasks(), engineer.tasks());
  }

  @Test
  void deserializeManager() {
    // When
    var manager = TestData.json.deserialize(TestData.ANNIKA_MANAGER_JSON, Manager.class).get();
    // Then
    Assertions.assertEquals(TestData.ANNIKA_MANAGER.name(), manager.name());
    Assertions.assertEquals(TestData.BOB_ENGINEER, manager.team().get(0));
  }

  @Test
  void deserializeNameCreated() {
    // When
    var person = TestData.json.deserialize(TestData.NAME_CREATED_JSON, NameCreated.class).get();
    // Then
    Assertions.assertEquals(TestData.NAME_CREATED, person);
  }

  @Test
  void deserializePerson() {
    // When
    var person = TestData.json.deserialize(TestData.BOB_PERSON_JSON, Person.class).get();
    // Then
    Assertions.assertEquals(TestData.BOB_PERSON, person);
  }

  @Test
  void serializeNameCreated() {
    // When
    var bob = TestData.json.serialize(TestData.NAME_CREATED).get();
    // Then
    assertEquals(TestData.NAME_CREATED_JSON, bob);
  }

  @Test
  void serializePerson() {
    // When
    var bob = TestData.json.serialize(TestData.BOB_PERSON).get();
    // Then
    assertEquals(TestData.BOB_PERSON_JSON, bob);
  }
}
