package io.memoria.jutils.jcore.eventsourcing.data.user;

public interface User {
  record Account(String name) implements User {}

  record Visitor() implements User {}
}
