package io.memoria.jutils.core.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.core.domain.port.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalDateGsonAdapterTest {
  private Gson gson = LocalDateGsonAdapter.register(new GsonBuilder(), ofPattern("yyyy-MM-dd")).create();
  private Json j = new JsonGson(gson);

  // Date data
  private LocalDate dateObj = LocalDate.of(2018, 11, 24);
  private String jsonDate = "\"2018-11-24\"";
  private String dateString = "2018-11-24";

  @Test
  public void dateSerializer() {
    String result = j.toJsonString(dateObj);
    assertEquals(dateString, result);
  }

  @Test
  public void dateDeserializer() {
    LocalDate result = j.toObject(jsonDate, LocalDate.class).get();
    assertEquals(dateObj, result);
  }
}
