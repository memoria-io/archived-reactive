package io.memoria.jutils.messaging.domain;

import io.vavr.control.Option;

import static io.vavr.control.Option.none;

public record Message(Option<String>id, String message) {
  public Message(String message) {
    this(none(), message);
  }
}
