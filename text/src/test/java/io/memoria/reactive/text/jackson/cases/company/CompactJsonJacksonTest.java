package io.memoria.reactive.text.jackson.cases.company;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static io.memoria.reactive.text.jackson.TestDeps.compactJson;

class CompactJsonJacksonTest {

  @Test
  void deserializePerson() {
    // When
    var personMono = compactJson.deserialize(CompanyData.COMPACT_BOB_PERSON_JSON, Person.class);
    // Then
    StepVerifier.create(personMono).expectNext(CompanyData.BOB_PERSON).verifyComplete();
  }

  @Test
  void serializePerson() {
    // When
    var bobMono = compactJson.serialize(CompanyData.BOB_PERSON);
    // Then
    assert CompanyData.COMPACT_BOB_PERSON_JSON != null;
    StepVerifier.create(bobMono).expectNext(CompanyData.COMPACT_BOB_PERSON_JSON).verifyComplete();
  }
}
