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

  // Date data
  private final LocalDate dateObj = LocalDate.of(2018, 11, 24);
  private final String jsonDate = "\"2018-11-24\"";
  private final String dateString = "2018-11-24";

  @Test
  public void deserializer() {
    LocalDate result = j.toObject(jsonDate, LocalDate.class).get();
    assertEquals(dateObj, result);
  }

  @Test
  public void serializer() {
    String result = j.toString(dateObj);
    assertEquals(dateString, result);
  }
}
