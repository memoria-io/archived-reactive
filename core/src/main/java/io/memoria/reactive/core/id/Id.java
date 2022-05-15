package io.memoria.reactive.core.id;

import java.io.Serializable;
import java.util.UUID;

public interface Id extends Serializable {
  String value();

  static Id of() {
    return of(UUID.randomUUID());
  }

  static Id of(UUID id) {
    return new DefaultId(id.toString());
  }

  static Id of(long id) {
    return new DefaultId(Long.toString(id));
  }

  static Id of(String id) {
    return new DefaultId(id);
  }
}
