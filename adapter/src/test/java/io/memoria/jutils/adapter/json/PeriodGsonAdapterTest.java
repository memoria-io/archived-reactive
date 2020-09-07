package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PeriodGsonAdapterTest {
  private final Gson gson = PeriodGsonAdapter.register(new GsonBuilder()).create();
  private final Json j = new JsonGson(gson);
  private final String json = "\"P1Y2M25D\"";
  private final String str = "P1Y2M25D";
  private final Period period = Period.parse(str);

  @Test
  public void deserializer() {
    Period actual = j.toObject(json, Period.class).get();
    assertEquals(period, actual);
  }

  @Test
  public void serializer() {
    String actual = j.toString(period);
    assertEquals(str, actual);
  }
}
