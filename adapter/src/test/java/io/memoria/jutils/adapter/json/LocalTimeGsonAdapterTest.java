package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalTimeGsonAdapterTest {
  private final Gson gson = LocalTimeGsonAdapter.register(new GsonBuilder(),
                                                          DateTimeFormatter.ISO_LOCAL_TIME,
                                                          ZoneOffset.UTC).create();
  private final Json j = new JsonGson(gson);
  private final String json = "\"18:04:04.2022\"";
  private final String str = "18:04:04.2022";
  private final LocalTime timeObj = LocalTime.parse(str, DateTimeFormatter.ISO_LOCAL_TIME)
                                             .atOffset(ZoneOffset.UTC)
                                             .toLocalTime();

  @Test
  public void deserializer() {
    LocalTime actual = j.toObject(json, LocalTime.class).get();
    assertEquals(timeObj, actual);
  }

  @Test
  public void serializer() {
    String actual = j.toString(timeObj);
    assertEquals(str, actual);
  }
}
