package io.memoria.jutils.jcore.eventsourcing;

import io.memoria.jutils.jcore.eventsourcing.User.Account;
import io.memoria.jutils.jcore.eventsourcing.User.Visitor;

public record UserEvolver() implements Evolver<User> {
  @Override
  public User apply(User user, Event event) {
    if (user instanceof Visitor && event instanceof UserCreated e) {
      return new Account(e.username());
    }
    return user;
  }
}
