package io.memoria.jutils.messaging.domain.entity;

import io.memoria.jutils.core.domain.port.crud.Storable;

public interface Msg extends Storable<String> {
  String message();
}
