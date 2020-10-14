package io.memoria.jutils.adapter.json;

import io.memoria.jutils.adapter.transformer.json.JsonGson;
import io.memoria.jutils.adapter.transformer.json.PeriodAdapter;
import io.memoria.jutils.core.transformer.json.Json;
import org.junit.jupiter.api.Test;

import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PeriodAdapterTest {
  private final Json parser = new JsonGson(new PeriodAdapter());
  // Given
  private final String periodJson = "\"P1Y2M25D\"";
  private final Period period = Period.of(1, 2, 25);

  @Test
  void deserializer() {
    // When
    Period deserializedPeriod = parser.deserialize(periodJson, Period.class).get();
    // Then
    assertEquals(period, deserializedPeriod);
  }

  @Test
  void serializer() {
    // When
    String serializedPeriod = parser.serialize(period).get();
    // Then
    assertEquals(periodJson, serializedPeriod);
  }
}
