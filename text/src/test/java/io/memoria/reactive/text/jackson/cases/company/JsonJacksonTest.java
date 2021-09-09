package io.memoria.reactive.text.jackson.cases.company;

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static io.memoria.reactive.text.jackson.TestDeps.json;

class JsonJacksonTest {

  @Test
  void deserializeDepartment() {
    // Given
    var expectedDepartment = new Department(List.of(CompanyData.ANNIKA_MANAGER,
                                                    CompanyData.BOB_ENGINEER,
                                                    CompanyData.ALEX_ENGINEER));
    // When
    var actualDepartment = json.deserialize(CompanyData.DEPARTMENT_JSON, Department.class);
    // Then
    StepVerifier.create(actualDepartment).expectNext(expectedDepartment).verifyComplete();
  }

  @Test
  void deserializeEngineer() {
    // When
    var engineerMono = json.deserialize(CompanyData.BOB_ENGINEER_JSON, Engineer.class);
    // Then
    StepVerifier.create(engineerMono).expectNext(CompanyData.BOB_ENGINEER).verifyComplete();
  }

  @Test
  void deserializeManager() {
    // When
    var managerMono = json.deserialize(CompanyData.ANNIKA_MANAGER_JSON, Manager.class);
    // Then
    StepVerifier.create(managerMono).expectNext(CompanyData.ANNIKA_MANAGER).verifyComplete();
    StepVerifier.create(managerMono.map(Manager::team))
                .expectNext(List.of(CompanyData.BOB_ENGINEER, CompanyData.ALEX_ENGINEER))
                .verifyComplete();
  }

  @Test
  void deserializeNameCreated() {
    // When
    var personMono = json.deserialize(CompanyData.NAME_CREATED_JSON, NameCreated.class);
    // Then
    StepVerifier.create(personMono).expectNext(CompanyData.NAME_CREATED).verifyComplete();
  }

  @Test
  void deserializePerson() {
    // When
    var personMono = json.deserialize(CompanyData.BOB_PERSON_JSON, Person.class);
    // Then
    StepVerifier.create(personMono).expectNext(CompanyData.BOB_PERSON).verifyComplete();
  }

  @Test
  void serializeNameCreated() {
    // When
    var bobMono = json.serialize(CompanyData.NAME_CREATED);
    // Then
    assert CompanyData.NAME_CREATED_JSON != null;
    StepVerifier.create(bobMono).expectNext(CompanyData.NAME_CREATED_JSON).verifyComplete();
  }

  @Test
  void serializePerson() {
    // When
    var bobMono = json.serialize(CompanyData.BOB_PERSON);
    // Then
    assert CompanyData.BOB_PERSON_JSON != null;
    StepVerifier.create(bobMono).expectNext(CompanyData.BOB_PERSON_JSON).verifyComplete();
  }
}
