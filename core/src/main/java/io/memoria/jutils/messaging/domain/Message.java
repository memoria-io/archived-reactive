package io.memoria.jutils.messaging.domain;

import io.vavr.control.Option;

import java.time.LocalDateTime;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public record Message(String value, Option<String>id, Option<LocalDateTime>creationTime) {

  public Message(String value) {
    this(value, none(), none());
  }

  public Message withCreationTime(LocalDateTime time) {
    return new Message(value, id, some(time));
  }

  public Message withId(long id) {
    return withId(String.valueOf(id));
  }

  public Message withId(String id) {
    return new Message(value, some(id), creationTime);
  }
}
