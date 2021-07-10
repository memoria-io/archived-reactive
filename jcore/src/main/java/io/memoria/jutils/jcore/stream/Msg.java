package io.memoria.jutils.jcore.stream;

import io.memoria.jutils.jcore.id.Id;

import java.io.Serializable;

public interface Msg extends Serializable {
  static Msg of(Id id, String body) {
    return new DefaultMsg(id, body);
  }

  Id id();

  String body();
}
