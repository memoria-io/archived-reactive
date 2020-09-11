package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationAdapterTest {
  private final Gson gson = Tests.registerDurationAdapter(new GsonBuilder()).create();
  private final Json j = new JsonGson(gson);
  // Given
  private final String durationJson = "\"PT51H4M\"";
  private final Duration duration = Duration.ofHours(51).plusMinutes(4);

  @Test
  public void deserializer() {
    // When
    Duration actual = j.fromJson(durationJson, Duration.class).get();
    // Then
    assertEquals(duration, actual);
  }

  @Test
  public void serializer() {
    // When
    String actual = j.toJson(duration);
    // Then
    assertEquals(durationJson, actual);
  }
}
