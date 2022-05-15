package io.memoria.reactive.text.jackson.cases.company;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static io.memoria.reactive.text.jackson.TestDeps.compactJson;

class PersonTest {
  private static String text = "{\"name\":\"bob\",\"friendsIds\":[\"1\",\"2\",\"3\"],\"familyIds\":[\"1\",\"2\",\"3\"]}";

  @Test
  void deserializePerson() {
    // When
    var personMono = compactJson.deserialize(text, Person.class);
    // Then
    StepVerifier.create(personMono).expectNext(CompanyData.BOB_PERSON).verifyComplete();
  }

  @Test
  void serializePerson() {
    // When
    var bobMono = compactJson.serialize(CompanyData.BOB_PERSON);
    // Then
    StepVerifier.create(bobMono).expectNext(text).verifyComplete();
  }
}
