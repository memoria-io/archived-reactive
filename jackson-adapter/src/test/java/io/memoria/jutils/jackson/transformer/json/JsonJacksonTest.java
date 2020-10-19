package io.memoria.jutils.jackson.transformer.json;

import io.memoria.jutils.jackson.Tests;
import io.memoria.jutils.jackson.transformer.Employee.Engineer;
import io.memoria.jutils.jackson.transformer.Employee.Manager;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonJacksonTest {

  @Test
  void toEmployee() {
    // When
    var engineer = Tests.json.deserialize(Tests.JSON_ENGINEER, Engineer.class).get();
    // Then
    Assertions.assertEquals("bob", engineer.name());
    Assertions.assertEquals(List.of("fix issue 1", "Fix issue 2"), engineer.tasks());
  }

  @Test
  void toList() {
    // When
    var list = Tests.json.deserialize(Tests.JSON_LIST, String[].class).get();
    // Then
    assertEquals(List.of("mercedes", "chevy", "porsche"), List.of(list));
  }

  @Test
  void toManager() {
    // When
    var manager = Tests.json.deserialize(Tests.JSON_MANAGER, Manager.class).get();
    // Then
    Assertions.assertEquals("Annika", manager.name());
    Assertions.assertEquals(new Engineer("bob", List.of("fix issue 1", "Fix issue 2")), manager.team().get(0));
  }
}
