package io.memoria.jutils.adapter.json;

import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DurationAdapterTest {
  private final Json j = new JsonGson(new DurationAdapter());
  // Given
  private final String durationJson = "\"PT51H4M\"";
  private final Duration duration = Duration.ofHours(51).plusMinutes(4);

  @Test
  void deserializer() {
    // When
    Duration actual = j.deserialize(durationJson, Duration.class).get();
    // Then
    assertEquals(duration, actual);
  }

  @Test
  void serializer() {
    // When
    String actual = j.serialize(duration);
    // Then
    assertEquals(durationJson, actual);
  }
}
