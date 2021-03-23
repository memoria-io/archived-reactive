package io.memoria.jutils.jkafka.data.user;

public interface User {
  record Account(String name) implements User {}

  record Visitor() implements User {}
}
