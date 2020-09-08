package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalDateGsonAdapterTest {
  private final Gson gson = LocalDateGsonAdapter.register(new GsonBuilder(), ofPattern("yyyy-MM-dd")).create();
  private final Json j = new JsonGson(gson);

  // Given
  private final LocalDate dateObj = LocalDate.of(2018, 11, 24);
  private final String jsonDate = "\"2018-11-24\"";
  private final String dateString = "2018-11-24";

  @Test
  public void deserializer() {
    // When
    LocalDate result = j.fromJson(jsonDate, LocalDate.class).get();
    // Then
    assertEquals(dateObj, result);
  }

  @Test
  public void serializer() {
    // When
    String result = j.toJson(dateObj);
    // Then
    assertEquals(dateString, result);
  }
}
