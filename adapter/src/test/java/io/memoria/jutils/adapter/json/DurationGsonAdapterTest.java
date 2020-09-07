package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationGsonAdapterTest {
  private final Gson gson = DurationGsonAdapter.register(new GsonBuilder()).create();
  private final Json j = new JsonGson(gson);
  private final String json = "\"PT51H4M\"";
  private final String str = "PT51H4M";
  private final Duration duration = Duration.parse(str);

  @Test
  public void deserializer() {
    Duration actual = j.toObject(json, Duration.class).get();
    assertEquals(duration, actual);
  }

  @Test
  public void serializer() {
    String actual = j.toString(duration);
    assertEquals(str, actual);
  }
}
