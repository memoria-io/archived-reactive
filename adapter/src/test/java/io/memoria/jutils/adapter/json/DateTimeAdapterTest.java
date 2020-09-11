package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateTimeAdapterTest {
  private final Gson gson = Tests.registerDateTime(new GsonBuilder(),
                                                   DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                                                   ZoneOffset.UTC).create();
  private final Json j = new JsonGson(gson);
  // Given
  private final String dateTimeJson = "\"2018-11-24T04:04:00\"";
  private final LocalDateTime dateTimeObj = LocalDateTime.of(2018, 11, 24, 4, 4);

  @Test
  public void deserializer() {
    // When
    LocalDateTime deserializedDateTime = j.deserialize(dateTimeJson, LocalDateTime.class).get();
    // Then
    assertEquals(dateTimeObj, deserializedDateTime);
  }

  @Test
  public void serializer() {
    // When
    String serializedDateTime = j.serialize(dateTimeObj);
    // Then
    assertEquals(dateTimeJson, serializedDateTime);
  }
}
