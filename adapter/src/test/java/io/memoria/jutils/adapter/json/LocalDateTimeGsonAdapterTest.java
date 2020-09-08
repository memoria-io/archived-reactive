package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalDateTimeGsonAdapterTest {
  private final Gson gson = LocalDateTimeGsonAdapter.register(new GsonBuilder(),
                                                              DateTimeFormatter.ISO_DATE_TIME,
                                                              ZoneOffset.UTC).create();
  private final Json j = new JsonGson(gson);
  // Given
  private final String dateTimeString = "2018-11-24T18:04:04.298956Z";
  private final String dateTimeJson = "\"2018-11-24T18:04:04.298956Z\"";
  private final LocalDateTime dateTimeObj = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
                                                         .atOffset(ZoneOffset.UTC)
                                                         .toLocalDateTime();

  @Test
  public void deserializer() {
    // When
    LocalDateTime deserializedDateTime = j.fromJson(dateTimeJson, LocalDateTime.class).get();
    // Then
    assertEquals(dateTimeObj, deserializedDateTime);
  }

  @Test
  public void serializer() {
    // When
    String serializedDateTime = j.toJson(dateTimeObj);
    // Then
    assertEquals(dateTimeString, serializedDateTime);
  }
}
