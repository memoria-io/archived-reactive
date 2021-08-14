package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;

import java.io.Serializable;

public record Msg(Id id, String body) implements Serializable {
  public static Msg of(long id, String body) {
    return new Msg(Id.of(id), body);
  }

  public static Msg of(String id, String body) {
    return new Msg(Id.of(id), body);
  }
}
