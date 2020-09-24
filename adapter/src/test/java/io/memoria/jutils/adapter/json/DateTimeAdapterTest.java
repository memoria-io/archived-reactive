package io.memoria.jutils.adapter.json;

import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeAdapterTest {
  private final Json j = new JsonGson(new DateTimeAdapter(DateTimeFormatter.ISO_LOCAL_DATE_TIME, ZoneOffset.UTC));
  // Given
  private final String dateTimeJson = "\"2018-11-24T04:04:00\"";
  private final LocalDateTime dateTimeObj = LocalDateTime.of(2018, 11, 24, 4, 4);

  @Test
  void deserializer() {
    // When
    LocalDateTime deserializedDateTime = j.deserialize(dateTimeJson, LocalDateTime.class).get();
    // Then
    assertEquals(dateTimeObj, deserializedDateTime);
  }

  @Test
  void serializer() {
    // When
    String serializedDateTime = j.serialize(dateTimeObj);
    // Then
    assertEquals(dateTimeJson, serializedDateTime);
  }
}
