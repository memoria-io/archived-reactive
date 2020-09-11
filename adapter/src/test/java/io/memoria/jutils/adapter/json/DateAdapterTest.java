package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateAdapterTest {
  private final Gson gson = Tests.registerDate(new GsonBuilder(), ofPattern("yyyy-MM-dd")).create();
  private final Json j = new JsonGson(gson);

  // Given
  private final LocalDate dateObj = LocalDate.of(2018, 11, 24);
  private final String jsonDate = "\"2018-11-24\"";

  @Test
  public void deserializer() {
    // When
    LocalDate result = j.deserialize(jsonDate, LocalDate.class).get();
    // Then
    assertEquals(dateObj, result);
  }

  @Test
  public void serializer() {
    // When
    String result = j.serialize(dateObj);
    // Then
    System.out.println(result);
    System.out.println(jsonDate);
    assertEquals(jsonDate, result);
  }
}
