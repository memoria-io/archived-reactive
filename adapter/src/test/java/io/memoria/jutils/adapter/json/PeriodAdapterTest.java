package io.memoria.jutils.adapter.json;

import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PeriodAdapterTest {
  private final Json parser = new JsonGson(new PeriodAdapter());
  // Given
  private final String periodJson = "\"P1Y2M25D\"";
  private final Period period = Period.of(1, 2, 25);

  @Test
  public void deserializer() {
    // When
    Period deserializedPeriod = parser.deserialize(periodJson, Period.class).get();
    // Then
    assertEquals(period, deserializedPeriod);
  }

  @Test
  public void serializer() {
    // When
    String serializedPeriod = parser.serialize(period);
    // Then
    assertEquals(periodJson, serializedPeriod);
  }
}
