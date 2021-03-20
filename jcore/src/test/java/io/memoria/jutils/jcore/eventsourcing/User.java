package io.memoria.jutils.jcore.eventsourcing;

interface User {
  record Account(String name) implements User {}

  record Visitor() implements User {}
}
