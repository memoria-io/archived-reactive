package io.memoria.jutils.jcore.usecase.user;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.eventsourcing.Evolver;
import io.memoria.jutils.jcore.usecase.user.User.Account;
import io.memoria.jutils.jcore.usecase.user.User.Visitor;
import io.memoria.jutils.jcore.usecase.user.UserEvent.MessageSent;

public record UserEvolver() implements Evolver<User> {
  @Override
  public User apply(User user, Event event) {
    if (user instanceof Visitor && event instanceof UserEvent.UserCreated e) {
      return new Account(e.name());
    }
    if (user instanceof Account acc && event instanceof MessageSent e) {
      return acc.withMessage(e.receiverId(), e.message());
    }
    return user;
  }
}
