package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeAdapterTest {
  private final Gson gson = Tests.registerTime(new GsonBuilder(), DateTimeFormatter.ISO_LOCAL_TIME, ZoneOffset.UTC)
                                 .create();
  private final Json parser = new JsonGson(gson);
  // Given
  private final String timeString = "18:04:04.2022";
  private final String timeJson = "\"18:04:04.2022\"";
  private final LocalTime timeObj = LocalTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_TIME)
                                             .atOffset(ZoneOffset.UTC)
                                             .toLocalTime();

  @Test
  public void deserializer() {
    // When
    LocalTime deserializedTime = parser.fromJson(timeJson, LocalTime.class).get();
    // Then
    assertEquals(timeObj, deserializedTime);
  }

  @Test
  public void serializer() {
    // When
    String serializedJson = parser.toJson(timeObj);
    // Then
    assertEquals(timeString, serializedJson);
  }
}
