package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;

import java.io.Serializable;

public record Msg(String topic, int partition, Id id, String value) implements Serializable {
  public Msg {
    if (partition < 0) {
      throw new IllegalArgumentException("partition field is set to %d which is below 0".formatted(partition));
    }
  }
}