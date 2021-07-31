package io.memoria.reactive.core.stream;

import io.memoria.reactive.core.id.Id;

import java.io.Serializable;

public interface Msg extends Serializable {
  static Msg of(Id id, String body) {
    return new DefaultMsg(id, body);
  }

  String body();

  Id id();
}
