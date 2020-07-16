package io.memoria.jutils.core.messaging;

import io.vavr.control.Option;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public record Message(String value, Option<Long>id) {

  public Message(String value) {
    this(value, none());
  }

  public Message(String value, long id) {
    this(value, some(id));
  }

  public Message withId(long id) {
    return new Message(this.value, id);
  }
}
