package io.memoria.jutils.adapter.json;

import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DateAdapterTest {
  private final Json j = new JsonGson(new DateAdapter(ofPattern("yyyy-MM-dd")));

  // Given
  private final LocalDate dateObj = LocalDate.of(2018, 11, 24);
  private final String jsonDate = "\"2018-11-24\"";

  @Test
  void deserializer() {
    // When
    LocalDate result = j.deserialize(jsonDate, LocalDate.class).get();
    // Then
    assertEquals(dateObj, result);
  }

  @Test
  void serializer() {
    // When
    String result = j.serialize(dateObj);
    // Then
    System.out.println(result);
    System.out.println(jsonDate);
    assertEquals(jsonDate, result);
  }
}
