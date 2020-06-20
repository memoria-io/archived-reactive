package io.memoria.jutils.messaging.domain;

import io.memoria.jutils.core.domain.port.crud.Storable;

public interface Message extends Storable<String> {
  String message();
}
