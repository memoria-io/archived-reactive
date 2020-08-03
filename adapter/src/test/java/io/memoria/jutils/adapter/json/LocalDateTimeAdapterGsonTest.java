package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalDateTimeAdapterGsonTest {
  private final Gson gson = LocalDateTimeGsonAdapter.register(new GsonBuilder(),
                                                              DateTimeFormatter.ISO_DATE_TIME,
                                                              ZoneOffset.UTC).create();
  private final Json j = new JsonGson(gson);
  // DateTfinal ime data
  private final String json = "\"2018-11-24T18:04:04.298956Z\"";
  private final String str = "2018-11-24T18:04:04.298956Z";
  private final LocalDateTime dateTimeObj = LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME)
                                                         .atOffset(ZoneOffset.UTC)
                                                         .toLocalDateTime();

  @Test
  public void dateTimeDeserializer() {
    LocalDateTime actual = j.toObject(json, LocalDateTime.class).get();
    assertEquals(dateTimeObj, actual);
  }

  @Test
  public void dateTimeSerializer() {
    String actual = j.toString(dateTimeObj);
    assertEquals(str, actual);
  }
}
