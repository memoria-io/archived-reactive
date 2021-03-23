package io.memoria.jutils.jcore.eventsourcing.data.user;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.Evolver;
import io.memoria.jutils.jcore.eventsourcing.data.user.User.Account;
import io.memoria.jutils.jcore.eventsourcing.data.user.User.Visitor;

public record UserEvolver() implements Evolver<User> {
  @Override
  public User apply(User user, Event event) {
    if (user instanceof Visitor && event instanceof UserEvent.UserCreated e) {
      return new Account(e.name());
    }
    return user;
  }
}
